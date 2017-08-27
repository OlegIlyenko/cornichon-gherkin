## cornichon-gherkin

[![Build Status](https://travis-ci.org/OlegIlyenko/cornichon-gherkin.svg?branch=master)](https://travis-ci.org/OlegIlyenko/cornichon-gherkin)

[Cornichon](http://agourlay.github.io/cornichon/) - [Gherkin](https://github.com/cucumber/cucumber/wiki/Gherkin) integration. Combines clean extensible
functional core from [Cornichon](http://agourlay.github.io/cornichon/) with great BDD feature definition language -
[Gherkin](https://github.com/cucumber/cucumber/wiki/Gherkin).

### Basic setup

Add following SBT dependency:

```scala
libraryDependencies += "com.github.olegilyenko" %% "cornichon-gherkin" % "0.0.0"
```

As version indicates, it's still POC.

Now you can create a normal test class and extend `GherkinBasedFeature` to. The name of the feature file is inferred
based on the test class name. So, for instance, for `StarWarsFeature` the feature fine would be `starWars.feature`. You can
override this behaviour by overriding `featureFile` method. I would recommend to your `*.feature` files in `src/test/resources` folder.

Here is an example of basic test class [`StarWarsFeature`](https://github.com/OlegIlyenko/cornichon-gherkin/blob/master/src/test/scala/cornichonGherkin/StarWarsFeature.scala):

```scala
class StarWarsFeature extends GherkinBasedFeature {
  lazy val stepDefinitions = Steps {
    step"I get ${strArg.ph}" { url ⇒
      When I get(url.toString)
    }

    step"response code is $intArg" { code ⇒
      Then assert status.is(code)
    }

    // ...

    After("@showSession") {
      show_session
    }
  }
}
```

And complementary feature file [`starWars.feature`](https://github.com/OlegIlyenko/cornichon-gherkin/blob/master/src/test/resources/starWars.feature):

```cucumber
Feature: Star Wars API

  Scenario: check out Luke Skywalker
    When I get http://swapi.co/api/people/1/
    Then response code is 200
    And response body with whitelisting is
    """
    {
      "name": "Luke Skywalker",
      "height": "172",
      "mass": "77",
      "hair_color": "blond",
      "skin_color": "fair",
      "eye_color": "blue",
      "birth_year": "19BBY",
      "gender": "male",
      "homeworld": "http://swapi.co/api/planets/1/"
    }
    """
    And I save path 'homeworld' as 'homeworld-url'

    ...
```

### Tags

Out-of-the-box supported tags:

* `@ignore` - ignore `Scenario` or `Feature`
* `@pending` - mark `Scenario` or `Feature` as pending (they would be ignored as well)
* `@focus` - only focused scenarios are executed. All other `Scenario`s without this tag would be ignored.

You can define additional steps `Before`, `After` or `Around` scenarios annotated with particular tag. Here is an example:

```scala
Steps {
  Before("@debug") {
    show_session
  }

  After("@debug") {
    show_session
  }

  Around("@moDebugging") { steps ⇒
    StepList {
      Then I print_step("Printing before")

      steps

      Then I print_step("Printing after")
    }
  }
}
```
