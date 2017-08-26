## cornichon-gherkin

[Cornichon](http://agourlay.github.io/cornichon/) - [Gherkin](https://github.com/cucumber/cucumber/wiki/Gherkin) integration. Combines clean extensible
functional core from [Cornichon](http://agourlay.github.io/cornichon/) with great BDD feature definition language -
[Gherkin](https://github.com/cucumber/cucumber/wiki/Gherkin).

### Tags

Out-of-the-box supported tags:

* `@ignore` - ignore `Scenario` or `Feature`
* `@pending` - mark `Scenario` or `Feature` as pending (they would be ignored as well)
* `@focus` - only focused scenarios are executed. All other `Scenario`s without this tag would be ignored.

You can define additional steps `Before`, `After` or `Around` scenarios annotated with particular tag. Here is an example:


