@kitchen
Feature: Location

  Scenario Outline: Add location
    Given user "<userName>" identified by "<password>" is logged in
    When post "/location" into "location"
      """
      {
      "name": "string",
      "details": "string"
      }
      """
    Then response status <status>
    And exists in the "location" response "<property>"
    And exists in the "location" response "details" with string value "string"
    And exists in the "location" response with external "location_ok.json"
    Examples:
      | userName          | password | status | property |
      | kadm@yokudlela.hu | kitchen  | 201    | name     |

  Scenario Outline: Add location - json
    Given user "<userName>" identified by "<password>" is logged in
    When post "/location" with external "location_ok.json"
    Then response status <status>
    Examples:
      | userName           | password   | status |
      | kadm@yokudlela.hu  | kitchen    | 201    |
      | dmadm@yokudlela.hu | daily-menu | 403    |

  Scenario: Add location - bad req
    Given user "kadm@yokudlela.hu" identified by "kitchen" is logged in
    When post "/location" into "location"
    """
    {
    "name": "",
    "details": "string"
    }
    """
    Then response status 400

  Scenario: Add location - redirect
    When post "/location"
      """
      {
      "name": "string",
      "details": "string"
      }
      """
    Then response status 302

  Scenario: Add location - forbidden
    Given user "dmadm@yokudlela.hu" identified by "daily-menu" is logged in
    When post "/location" with external "location_ok.json"
    Then response status 403

  Scenario: Get location
    Given user "kadm@yokudlela.hu" identified by "kitchen" is logged in
    Given post "/location" with external "location_ok.json" into "locationAdded"
    When get "/location/${response(locationAdded).id}" into "location"
    Then exists in the "location" response "details" with string value "string"
    And exists in the "location" response "id"
    And response status 200

  Scenario: Get location - redirect
    When get "/location/${random(1, 100)}"
    Then response status 302

  Scenario: Get location - not found
    Given user "kadm@yokudlela.hu" identified by "kitchen" is logged in
    When get "/location/${random(65665, 65666)}"
    Then response status 404

  Scenario: Get location - redirect
    When get "/location/${random(1, 100)}"
    Then response status 302

  Scenario: Get all locations
    Given user "kadm@yokudlela.hu" identified by "kitchen" is logged in
    When get "/location/all"
    Then response status 200

  Scenario: Get all locations - redirect
    When get "/location/all"
    Then response status 302

  Scenario: Put location
    Given user "kadm@yokudlela.hu" identified by "kitchen" is logged in
    Given post "/location" with external "location_ok.json" into "locationAdded"
    When put "/location/${response(locationAdded).id}" into "modified"
    """
    {
    "name": "modified-name",
    "details": "modified-details"
    }
    """
    Then response status 200
    And exists in the "modified" response "name" with string value "modified-name"

  Scenario: Put location - redirect
    When put "/location/${random(1, 100)}" with external "location_ok.json" into "modified"
    Then response status 302

  Scenario: Put location - forbidden
    Given user "dmadm@yokudlela.hu" identified by "daily-menu" is logged in
    When put "/location/${random(1, 100)}" with external "location_ok.json"
    Then response status 403

  Scenario: Put location - new
    Given user "kadm@yokudlela.hu" identified by "kitchen" is logged in
    When put "/location/${random(1111111, 1111112)}" into "modified"
    """
    {
    "name": "modified-name",
    "details": "modified-details"
    }
    """
    Then response status 201
    And exists in the "modified" response "name" with string value "modified-name"

  Scenario: Delete location
    Given user "kadm@yokudlela.hu" identified by "kitchen" is logged in
    Given post "/location" with external "location_ok.json" into "location"
    When delete "/location/${response(location).id}"
    Then response status 204

  Scenario: Delete location - redirect
    When delete "/location/${random(1, 100)}" into "deleted"
    Then response status 302

  Scenario: Delete location - forbidden
    Given user "dmadm@yokudlela.hu" identified by "daily-menu" is logged in
    When delete "/location/${random(1, 100)}"
    Then response status 403

