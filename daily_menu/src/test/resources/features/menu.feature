@menu
Feature: Menu

  # ! adds menu, only one menu can be added for one day: check JUnit tests if
  # ! Caused by: java.sql.SQLIntegrityConstraintViolationException: (conn=4) Duplicate entry '2023-04-13' for key 'UK_MENU_DAY'
  # ! happens
  Scenario: Add menu
    Given user "dmadm@yokudlela.hu" identified by "dmadm" is logged in
    When post "/api/v1/menu" with external "new_menu.json" into "menuAdded"
    Then response status 201
