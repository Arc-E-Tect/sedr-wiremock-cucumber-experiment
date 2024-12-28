Feature: Retrieve System Health Information

  This feature is used to retrieve system health information.
  The feature can be used to query the system in case other functionality is not working as expected.

  Scenario: Retrieve System Health Information
    Although we assume that the system is up and running, we still put it in the scenario and will check if the system is
    actually up and running. It makes no sense to retrieve health information from a system that is not running.

    Given the application is deployed
    And the system is up and running
    When the system health information is retrieved
    Then the system reports that it is "up and running"