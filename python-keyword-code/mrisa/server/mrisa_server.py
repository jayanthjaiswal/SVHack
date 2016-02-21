import pycurl, json
from flask import Flask, url_for, json, request
from StringIO import StringIO
from bs4 import BeautifulSoup
import nltk
from nltk.corpus import stopwords
from nltk import FreqDist
from sklearn.feature_extraction.text import TfidfVectorizer
import operator
import nltk
import string



app = Flask(__name__)



def isPunct(word):
  return len(word) == 1 and word in string.punctuation

def isNumeric(word):
  try:
    float(word) if '.' in word else int(word)
    return True
  except ValueError:
    return False

class RakeKeywordExtractor:

  def __init__(self):
    self.stopwords = set(nltk.corpus.stopwords.words())
    self.top_fraction = 1 # consider top third candidate keywords by score

  def _generate_candidate_keywords(self, sentences):
    phrase_list = []
    for sentence in sentences:
      words = map(lambda x: "|" if x in self.stopwords else x,
        nltk.word_tokenize(sentence.lower()))
      phrase = []
      for word in words:
        if word == "|" or isPunct(word):
          if len(phrase) > 0:
            phrase_list.append(phrase)
            phrase = []
        else:
          phrase.append(word)
    return phrase_list

  def _calculate_word_scores(self, phrase_list):
    word_freq = nltk.FreqDist()
    word_degree = nltk.FreqDist()
    for phrase in phrase_list:
      degree = len(filter(lambda x: not isNumeric(x), phrase)) - 1
      for word in phrase:
        word_freq[word] += 1
        word_degree[word] += degree # other words
    for word in word_freq.keys():
      word_degree[word] = word_degree[word] + word_freq[word] # itself
    # word score = deg(w) / freq(w)
    word_scores = {}
    for word in word_freq.keys():
      word_scores[word] = word_degree[word] / word_freq[word]
    return word_scores

  def _calculate_phrase_scores(self, phrase_list, word_scores):
    phrase_scores = {}
    for phrase in phrase_list:
      phrase_score = 0
      for word in phrase:
        phrase_score += word_scores[word]
      phrase_scores[" ".join(phrase)] = phrase_score
    return phrase_scores
    
  def extract(self, text, incl_scores=False):
    sentences = nltk.sent_tokenize(text)
    phrase_list = self._generate_candidate_keywords(sentences)
    word_scores = self._calculate_word_scores(phrase_list)
    phrase_scores = self._calculate_phrase_scores(
      phrase_list, word_scores)
    sorted_phrase_scores = sorted(phrase_scores.iteritems(),
      key=operator.itemgetter(1), reverse=True)
    n_phrases = len(sorted_phrase_scores)
    if incl_scores:
      return sorted_phrase_scores[0:int(n_phrases/self.top_fraction)]
    else:
      return map(lambda x: x[0],
        sorted_phrase_scores[0:int(n_phrases/self.top_fraction)])

def test(all_text):
  rake = RakeKeywordExtractor()
  keywords = rake.extract(all_text, incl_scores=True)
  print keywords


@app.route('/search', methods = ['POST'])
# first function called
def mrisa_main():
    # Detect the content type, only process if it's json, otherwise send an error
    if request.headers['Content-Type'] == 'application/json':
        client_json = json.dumps(request.json)
        client_data = json.loads(client_json)
        code = retrieve(client_data['image_url'])
        return google_image_results_parser(code)
        #return "JSON Message: " + json.dumps(request.json)
    else:
        json_error_message = "Requests need to be in json format. Please make sure the header is 'application/json' and the json is valid."
        return json_error_message

# retrieves the reverse search html for processing. This actually does the reverse image lookup
def retrieve(image_url):
    returned_code = StringIO()
    full_url = 'https://www.google.com/searchbyimage?&image_url=' + image_url
    conn = pycurl.Curl()
    conn.setopt(conn.URL, str(full_url))
    conn.setopt(conn.FOLLOWLOCATION, 1)
    conn.setopt(conn.USERAGENT, 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.97 Safari/537.11')
    conn.setopt(conn.WRITEFUNCTION, returned_code.write)
    conn.perform()
    conn.close()
    return returned_code.getvalue()

# Parses returned code (html,js,css) and assigns to array using beautifulsoup
def google_image_results_parser(code):
    soup = BeautifulSoup(code)

    # initialize 2d array
    whole_array = {'links':[],
                   'description':[],
                   'title':[],
                   'result_qty':[]}
    all_text = ''
    # Links for all the search results
    for li in soup.findAll('li', attrs={'class':'g'}):
        sLink = li.find('a')
        whole_array['links'].append(sLink['href'])

    # Search Result Description
    for desc in soup.findAll('span', attrs={'class':'st'}):
        whole_array['description'].append(desc.get_text())
        all_text += desc.get_text().lower()

    # Search Result Title
    for title in soup.findAll('h3', attrs={'class':'r'}):
        whole_array['title'].append(title.get_text())

    # Number of results
    for result_qty in soup.findAll('div', attrs={'id':'resultStats'}):
        whole_array['result_qty'].append(result_qty.get_text())

    test(all_text)

    return build_json_return(whole_array)

def build_json_return(whole_array):
    return json.dumps(whole_array)

if __name__ == '__main__':
    app.debug = True
    app.run(host='0.0.0.0')
