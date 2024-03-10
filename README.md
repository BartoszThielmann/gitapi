# gitapi

## Table of contents
* [Introduction](#Introduction)
* [Technologies](#Technologies)
* [Setup](#Setup)
* [Usage](#Usage)

## Introduction
This is a demo Spring project.

Acceptance criteria for the project are:

---

"As an api consumer, given username and header “Accept: application/json”, I would like to list all his github 
repositories, which are not forks. Information, which I require in the response, is:
- Repository Name
- Owner Login
- For each branch it’s name and last commit sha

As an api consumer, given not existing github user, I would like to receive 404 response in such a format:

```
{
  "status":${responseCode}
  "message":${whyHasItHappened}
}
```
Notes:
- Please full-fill the given acceptance criteria, delivering us your best code compliant with industry standards.
- Please use https://developer.github.com/v3 as a backing API
- Application should have a proper README.md file
---

## Technologies
Project was created with:
- Spring Boot 3
- Java 21
- MapStruct
- Mockito
- JUnit

## Setup
Make sure you have JDK 21 installed and that your JAVA_HOME is configured to use it.

The simplest way to run the app is to execute this command in project root:
```bash
./mvnw spring-boot:run
```

If startup will be successful, you'll see similar message in the logs:
```
Started GitapiApplication in 2.428 seconds (process running for 2.868)
```

## Usage
If the app is running, Swagger docs are available at /docs.html (http://localhost:8080/docs.html).

Requests can be sent with curl:
```bash
curl -L \
  -H "Accept: application/json" \
  http://localhost:8080/v1/repos/USERNAME
```
#### Headers:
`Accept` - setting to `application/json` is recommended

#### Path parameters:

`USERNAME` - fetches data for user with this username

---

#### Example request:
```bash
curl -L \
  -H "Accept: application/json" \
  http://localhost:8080/v1/repos/BartoszThielmann
```

Example response:
```
[
  {
    "repoName": "EmployeeManager",
    "ownerLogin": "BartoszThielmann",
    "branches": [
      {
        "name": "dev/rest",
        "sha": "e202e22f445c013085efd97cee1cb75b2bd0dae7"
      },
      {
        "name": "master",
        "sha": "047a63d066617208522cf341f17ce55116569abb"
      }
    ]
  },
  {
    "repoName": "GroceryList",
    "ownerLogin": "BartoszThielmann",
    "branches": [
      {
        "name": "dev/add_ingredients_functionality",
        "sha": "53e0e504b8f5f2006546649711d95f3080cfc797"
      },
      {
        "name": "floating_context_menu_for_recycler_view",
        "sha": "dea181bd98552fe147e121b33d12c0720f79000b"
      },
      {
        "name": "master",
        "sha": "17a7e0b6697bda58a864e48847d64cce85cfbf1f"
      }
    ]
  }
]
```

Example response when user doesn't exist:
```
{
  "status": 404,
  "message": "User does not exist"
}
```


