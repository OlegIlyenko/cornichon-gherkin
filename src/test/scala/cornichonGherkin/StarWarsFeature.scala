package cornichonGherkin

import cats.syntax.either._

class StarWarsFeature extends GherkinBasedFeature {
  lazy val stepDefinitions = Steps {
step"I get ${strArg.ph}" { url ⇒
  Attach {
    When I get(url.toString)
    Then assert status.is(200)
  }
}

    step"response code is $intArg" { code ⇒
      Then assert status.is(code)
    }

    step"response body $whitelist is: $docStrArg" { (whitelist, respBody) ⇒
      Then assert body.copy(whitelist = whitelist).is(respBody)
    }

    step"response body at path $strArg is: $strArg" { (path, value) ⇒
      Then assert body.path(path).is(value)
    }

    step"response body at path $strArg $whitelist is: $strTableArg" { (path, wl, value) ⇒
      Then assert body.copy(whitelist = wl).path(path).is(value)
    }

    step"I save path '$strArg' as '$strArg'" { (path, value) ⇒
      And I save_body_path(path → value)
    }

    After("@showSession") {
      show_session
    }
  }

  lazy val whitelist = strArg("with whitelisting").opt.transform(v ⇒ Right(v.isDefined))

  lazy val getParams = tableArg(
    Column.parseTable(_,
      ColumnGroup(matchAll, stringColumn("Param"), stringColumn("Value"))(
        (_, name, value) ⇒ Right(name → value)))
      .map(_ groupBy (_._1) mapValues (_.head._2)))
}