Feature: Dashboard, food tracking and pantry items
  As a user
  I want to create and edit pantry items and see the ones about to expire
  So that I can use them in time and waste less food

  Scenario: Add an item to the pantry
    Given I have completed the onboarding
    And the pantry is empty
    When I tap the "+ Add item" button
    And I insert "Milk" in the "Item name" field
    And I select the "Dairy" category
    And I insert "2" in the "Quantity" field
    And I select the "L" unit
    And I set the expiration date to "30/11/2026"
    And I tap the "Save item" button
    Then I see "Milk" in the pantry
    And I close the AUT

 Scenario: Show items expiring within 48 hours in the alert tray
    Given I have completed the onboarding
    And the pantry is empty
    When I add the item "Spinach" expiring tomorrow
    Then I see "Spinach" in the expiry alert tray
    And I see "Spinach" highlighted in the pantry
    And I close the AUT

  Scenario: Edit the quantity of a pantry item
    Given I have completed the onboarding
    And the pantry contains the item "Milk" with the quantity "2"
    When I tap the item "Milk"
    And I insert "1" in the "Quantity" field
    And I tap the "Save changes" button
    Then I see "Milk" with the quantity "1" in the pantry
    And I close the AUT