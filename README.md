# Kotlin + Spring Boot MVC Starter

## Batteries included

- Spring Boot 2 MVC
- Ready for JdbcTemplate-style repositories + ready PostgreSQL setup
- Database migrations with Flyway
- Spring Security email/password login + signup
- Thymeleaf templates for views + Layout dialect
- Sending emails and creating them using Thymeleaf templates
- Setup for unit testing with MockMvcTest, RepositoryTest, EmailTest, and normal JUnit4 tests for service layer
- Fast UI testing with Fluentlenium and HtmlUnit with FeatureTest
- Frontend module for your stylesheets with SCSS+Bootstrap4 included
- Example application with a few features implemented

## Table of contents

1. [Run the Demo app locally](#run-the-demo-app-locally)
1. [Running the tests](#running-the-tests)
1. [Familiarizing yourself with demo app structure](#familiarizing-yourself-with-demo-app-structure)
1. [Controller-Service-Repository pattern](#controller-service-repository-pattern)
1. [Removing the demo app code](#removing-the-demo-app-code)
1. [Removing the login-signup code if you do not need it](#removing-the-login-signup-code-if-you-do-not-need-it)
1. [Setting up the database](#setting-up-the-database)
1. [Setting up the database and email for deployment](#setting-up-the-database-and-email-for-deployment)

## Run the Demo app locally

First make sure, you have the `quizzy` and `quizzy_test` databases in your local PostgreSQL installation:

```bash
# needed to run the demo app locally
createdb quizzy
createuser quizzy

# needed to run tests
createdb quizzy_test
createuser quizzy_test
```

To run the app locally, you’ll need to activate the `dev` spring profile. For that provide the environment variable:

```bash
export SPRING_PROFILES_ACTIVE=dev
./gradlew bootRun
```

Once the app is done booting, you can visit [localhost:8080](http://localhost:8080) to see that it works.

Activating the `dev` profile gives you the following:

- When editing static files and Thymeleaf templates there is no need to restart the server,
  this allows for quicker development.
- When sending e-mails there is no need to provide real SMTP config, instead all e-mails will be just logged in the
  STDOUT of the running server (`./gradlew bootRun`).
- When uploading pictures, local filesystem will be used, instead of any 3rd party service.

To understand this better, take a look at [application-dev.yml](./src/main/resources/application-dev.yml),
and search the source code for the occurrence of `@Profile("dev")` and `@Profile("dev", "test")`.

Alternatively, you can run the application from the IntelliJ IDEA. For that go to `Application.kt`
and run the `main` function. This will fail because some spring beans will be missing.

You’ll need to set the `dev` spring profile. To do that, go to Run configurations -> Edit configurations
-> Kotlin -> app.ApplicationKt, then:

1. Hit the `Save Configuration` button.
1. Check the `Single instance only` checkbox.
1. Go to `Environment Variables` dialog.
1. Add `SPRING_PROFILES_ACTIVE` variable with value `dev`.
1. Hit `OK` button.

Now you should be able to run the application from IntelliJ IDEA.

## Running the tests

You can run all the tests with Gradle:

```bash
./gradlew test
```

If your setup is correct, then all the tests should pass.

Alternatively, you can run tests in IntelliJ IDEA by selecting the directory `src -> test -> kotlin`
and choosing `Run 'Tests' in 'kotlin'` from the context menu or by pressing the hot key to run the current selection
(for Mac: CMD+SHIFT+R, for Linux/Win: CTRL+SHIFT+R).

If you want to run specific package or class, you can do that as well in IntelliJ.

## Familiarizing yourself with demo app structure

Let’s begin from the top level:

```
PROJECT/
  frontend/          this is where SCSS+Bootstrap4 stylesheets live
  src/               this is where your back-end application lives
  build.gradle       this is where you define your dependencies with Gradle
```

Now, let’s dive into the structure of the production code for the back-end application:

```
src/
  main/
    kotlin/
      app/
        Application.kt                this is our Application class—entrypoint to the app
        config/                       config package contains general configuration of the web app
        email/                        email package contains code helping you send emails
        util/                         various helper functions and classes needed throughout the codebase
        auth/                         auth package contains security, login, signup, and logout concerns
        quiz/                         example demo application code [can be safely removed before you start]
    resources/
      application.yml                 your main application configuration file [edit to your liking]
      application-dev.yml             your local development configuration file
      application-cloud.yml.example   copy this file to application-cloud.yml and fill in the blanks [for deployment]
      db/
        migration/                    this package contains Flyway migration files
      static/ (soft-link)             this soft-link allows back-end to “see” the files generated by frontend module
      translations/
        messages*.properties          these files contain translations for different languages
      templates/
        layouts/                      this package contains Thymeleaf layouts (using layout dialect)
        emails/                       this package contains Thymeleaf email templates, render them with EmailTemplate helper
        auth/                         this package contains login, signup and logout related templates
        quizzes/                      this package contains demo app’s templates
```

The unit test side mirrors this structure exactly; more interesting are the feature tests:

```
src/
  test/
    kotlin/
      app/
        auth/
        email/
        quiz/
      featuretests/                 this is where all UI/feature tests live
        auth/                       feature tests for login, signup and logout
        quiz/                       feature tests for demo application
      helpers/
        FeatureTest                 class that provides default feature test configuration
        EmailTest                   class that provides default email test configuration
        MockMvcTest                 class that provides a standalone mock mvc controller test configuration
        RepositoryTest              class that provides default JdbcTemplate repository test configuration
      templates/
        emails/                     this package contains unit tests for email templates using EmailTemplate helper
```

Finally, let’s take a look at the front-end structure:

```
frontend/
  package.json                      this is where you define all your dependencies
  node_modules/                     this is where your front-end dependencies live, get these with `npm install`
  scss/
    src/                            this package is where your SCSS code lives
      index.scss                    your “root” file for stylesheets [run 'npm start' to compile & watch]
  static/                           this is where compiled stylesheets end up
```

## Controller-Service-Repository pattern

If you take a look at the `auth` or `quiz` packages you’ll see that there is a repeating pattern:

```
app/
  quiz/
    QuizController                      [Controller]
    QuizService                         [Service]
    QuizRepository                      [Repository]
    .. plus some data classes ..

  auth/
    signup/
      SignupController                  [Controller]
      ConfirmController                 [Controller]
      ConfirmationLinkService           [Service]
      ForceLoginService                 [Service]
      .. plus some data classes ..
    AuthService                         [Service]
    user/
      UserRepository                    [Repository]
```

- Controllers depend (via dependency injection) on Services, and call them.
- Controller is never calling the repository.
- Services depend (via dependency injection) on other services or repositories, and call them.
- Repositories depend only on JdbcTemplate.
  (You can also have JPA repositories here if you wanted, but I’ve found that they don’t scale very well,
   and create more trouble for you than saving in the long run, especially if you are unit-testing them).

I have found this pattern very useful on countless projects, and it is an architectural sweet spot
for most of the business domains. Moreover, when these three concepts are not enough, you can always
have services calling other services, thus the pattern can scale to any level of domain complexity.

<!--
Right now I’m creating the video screencast series “Kotlin on Back-end: Best practices with Spring Boot.” where
I’m going to cover this pattern, and much more. We’ll implement a full application together, and we’ll apply
best practices such as: Outside-in TDD, Dependency Injection, Controller-Service-Repository pattern, Clean code,
writing UI Feature tests that don't break all the time, and so much more.

Watch the [introduction video](https://iwillteachyoukotlin.com/kotlin-backend-best-practices) here.
-->

## Removing the demo app code

NOTE: This section is TODO. The scripts are not there yet.

To remove the demo app code, you can run a single shell-script:

```bash
./remove-demo.sh
```

## Removing the login-signup code if you do not need it

If you don’t need the classic login/signup code, you can remove it with a single shell-script:

```bash
./remove-auth.sh
```

## Setting up the database

After you have chosen the name for your development database (let’s pretend its name is `mydbname`),
you’ll need to create the database and the user to access it on your local postgres installation:

```bash
createdb mydbname
createuser mydbname

# and you’ll need a "_test" version of the db to use in the test suite:
createdb mydbname_test
createuser mydbname_test
```

Now, you’ll need to set this database name and user name in the `src/main/resources/application.yml`:

```yml
spring:
  datasource:
    url: jdbc:postgresql://localhost/mydbname
    username: mydbname
    password: mydbname
    driver-class-name: org.postgresql.Driver
```

Finally, you’ll need to set similar values for the test environment in the
`src/test/resources/application-test.yml`:

```yml
spring:
  datasource:
    url: jdbc:postgresql://localhost/mydbname_test
    username: mydbname_test
    password: mydbname_test
    driver-class-name: org.postgresql.Driver
```

## Setting up the database and email for deployment

Now, once you’ve decided how you will deploy your application, you could either provide an
`application-cloud.yml` configuration file (see example in `application-cloud.yml.example`),
or you could supply all the required variables through the environment variables, for example:

```bash
export SPRING_DATASOURCE_URL=<your db url>
export SPRING_DATASOURCE_USERNAME=<your db username>
export SPRING_DATASOURCE_PASSWORD=<your db password>

export SPRING_MAIL_HOST=<your smtp host>
export SPRING_MAIL_PORT=<your smtp port>
export SPRING_MAIL_USERNAME=<your smtp username>
export SPRING_MAIL_PASSWORD=<your smtp password>

export APP_AUTH_CONFIRMATION_EMAILS_FROM="Your Name <your-email@example.org>"
```

## Thanks!

Thank you for reading this and giving it a try.

To make me super happy you can star this repo and tweet about it!

<!--
And make sure to check out the [introduction video](https://iwillteachyoukotlin.com/kotlin-backend-best-practices)
about Kotlin Back-end best practices.
-->
