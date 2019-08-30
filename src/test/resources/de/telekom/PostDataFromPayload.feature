Feature: Post Data from Payload
  5c6d833f-5f6f-4abd-ba1e-7561a221336
  # activity ID from PostData: 588bbad8-7c4f-4f8e-9f74-7f97a25219d

  Scenario: User posts data to DB from payload
    Given the following payload
      | externalProcessIdAtCarrier             | 7854a     |
      | inquiryLocation.address.city           | Berlin    |
      | inquiryLocation.address.cityPart       | Spandau   |
      | inquiryLocation.address.postcode       | 13581     |
      | inquiryLocation.address.streetName     | Bager_Str |
      | inquiryLocation.address.streetNr       | 20        |
      | inquiryLocation.address.streetNrSuffix | A         |
      | inquiryLocation.klsId                  | 567       |
    When user sends this to gigabitAvailabilityInquiries
    Then the status code is 201
    And user gets activityId
    # the following line is to test that the data has actually been written to DB
    And the following data is obtained from DB after query
      | inquiryLocation.address.streetName     | Bager_Str |
      | inquiryLocation.address.streetNr       | 20        |
      | inquiryLocation.address.streetNrSuffix | A         |