Feature: Get Data From Activity ID
  5c6d833f-5f6f-4abd-ba1e-7561a221336

  Scenario: User calls microservice to get data from an activity ID
    Given activity ID 5c6d833f-5f6f-4abd-ba1e-7561a221336 exists
    When a user retrieves data from activity
    Then the status code is 200
    And response includes the following
      | activityId                         | 5c6d833f-5f6f-4abd-ba1e-7561a221336 |
      | externalProcessIdAtCarrier         | 12345f67890                         |
      | inquiryLocation.address.postcode   | 110001                              |
      | inquiryLocation.address.city       | TestCity                            |
      | inquiryLocation.address.streetName | MyStreet                            |
      | inquiryLocation.address.streetNr   | 1919                                |

  Scenario: User calls microservice to get data from a non-existing activity ID
    Given activity ID 123434-3fa57-452 does not exist
    When a user retrieves data from activity
    Then the status code is 404
    #And user gets information about the error