# Honeybadger Take-Home Project

### Running the project

I have dockerized the project, to reduce issues running it on other machines. 
Therefore, this will require [Docker](https://www.docker.com/) running on your local computer. 
Once you have cloned the repo, navigate to the directory and run the following commands.<br>

``docker build -t honeybadger .``<br>
``docker run -p 80:8080 --env SLACK_CHANNEL_WEBHOOK=<enter webhook> honeybadger``<br>

The application takes a Slack channel webhook as an environment variable argument to allow it to be more modular
for different Slack channels. I recognize that passing this as an environment variable is not ideal, since it
raises a security issue where the webhook could be revealed. However, for the sake of the project, since this
will only be run locally for testing, it shouldn't be an issue.

### Making requests to the endpoint

Testing the end point is fairly straightforward. The request url is ``http://localhost:80/slack-alerts/spam-emails``.
It requires the body of the request to be in the json format specified in the 
[take-home project description](https://honeybadger.notion.site/honeybadger/Take-home-project-for-Software-Developer-position-2023-fee9be3cd8454e1fb61e53f0172ff2e8).
If everything has been set up correctly, the request should return 'OK' and an alert should be sent to the Slack
channel based on the type in the payload.

### Contact Me

If there are any questions regarding the project, feel free to email me at madison.allen@siu.edu.