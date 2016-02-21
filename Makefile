virtualenv: virtualenv/bin/activate
virtualenv/bin/activate: requirements.txt
	test -d virtualenvenv || virtualenv virtualenv
	virtualenv/bin/pip install -Ur requirements.txt
	touch virtualenv/bin/activate
