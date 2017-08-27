Feature: Star Wars API
         see https://swapi.co/

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
    When I get <homeworld-url>
    Then response body with whitelisting is
    """
    {
      "name" : "Tatooine",
      "rotation_period" : "23",
      "orbital_period" : "304",
      "diameter" : "10465",
      "climate" : "arid",
      "gravity" : "1 standard",
      "terrain" : "desert",
      "surface_water" : "1",
      "population" : "200000"
    }
    """
    And I save path 'residents[0]' as 'first-resident'
    When I get <first-resident>
    Then response body at path name is: Luke Skywalker
    
  Scenario: Load list of planets
    When I get http://swapi.co/api/planets/
    And response body at path results with whitelisting is
      | name        | rotation_period | orbital_period | diameter | climate               |
      | "Alderaan"  | "24"            | "364"          | "12500"  | "temperate"           |
      | "Yavin IV"  | "24"            | "4818"         | "10200"  | "temperate, tropical" |
      | "Hoth"      | "23"            | "549"          | "7200"   | "frozen"              |
      | "Dagobah"   | "23"            | "341"          | "8900"   | "murky"               |
      | "Bespin"    | "12"            | "5110"         | "118000" | "temperate"           |
      | "Endor"     | "18"            | "402"          | "4900"   | "temperate"           |
      | "Naboo"     | "26"            | "312"          | "12120"  | "temperate"           |
      | "Coruscant" | "24"            | "368"          | "12240"  | "temperate"           |
      | "Kamino"    | "27"            | "463"          | "19720"  | "temperate"           |
      | "Geonosis"  | "30"            | "256"          | "11370"  | "temperate, arid"     |