Feature: Grocery shopping list
  As a user
  I want to keep a shopping list and scan or check the items I buy to move them into my pantry
  So that I know what to buy and keep my pantry up to date without typing everything again

  Scenario: Open the shopping list from the header
    Given I have completed the onboarding
    When I tap the shopping list button in the header
    Then I see the shopping list
    And I close the AUT

  Scenario: Add an item to the shopping list manually
    Given I have completed the onboarding
    And the shopping list is empty
    When I tap the shopping list button in the header
    And I insert "Eggs" in the "Item name" field
    And I tap the "Add" button
    Then I see "Eggs" in the shopping list
    And I close the AUT

  Scenario: Scan a product from the shopping list
    Given I have completed the onboarding
    And camera access is allowed
    And the shopping list contains the item "Nutella"
    When I tap the shopping list button in the header
    And I tap the scan button of the item "Nutella"
    And I scan the barcode "3017620422003"
        Then I see the scan notification "Nutella scanned"
    And I see the item "Nutella" checked
    And I close the AUT

  Scenario: Move scanned and checked items to the pantry
    Given I have completed the onboarding
    And camera access is allowed
    And the shopping list contains the items "Eggs" and "Nutella"
    When I tap the shopping list button in the header
    And I tap the scan button of the item "Nutella"
    And I scan the barcode "3017620422003"
    And I check the item "Eggs"
    And I tap the "Move to pantry" button
    Then I see the quantity "400" "g" filled in for "Nutella"
    When I create the category "Spreads" for "Nutella"
    And I set "Nutella" to the expiration date "31/12/2026"
    And I set "Eggs" to the category "Dairy", the quantity "12" "Units" and the expiration date "30/11/2026"
    And I tap the "Confirm" button
    Then I see "Nutella" in the pantry
    And I see "Eggs" in the pantry
    And I see the "Spreads" category in the pantry filters
    And I do not see "Nutella" in the shopping list
    And I do not see "Eggs" in the shopping list
    And I close the AUT
#
  Scenario: Transfer the checked items to the pantry
    Given I have completed the onboarding
    And the shopping list contains the items "Eggs" and "Butter"
    When I tap the shopping list button in the header
    And I check the item "Eggs"
    And I check the item "Butter"
    And I tap the "Move to pantry" button
    And I set "Eggs" to the category "Dairy", the quantity "12" "Units" and the expiration date "30/11/2026"
    And I set "Butter" to the category "Dairy", the quantity "250" "g" and the expiration date "15/12/2026"
    And I tap the "Confirm" button
    Then I see "Eggs" in the pantry
    And I see "Butter" in the pantry
    And I do not see "Eggs" in the shopping list
    And I do not see "Butter" in the shopping list
    And I close the AUT

  Scenario: Keep the unchecked items in the shopping list after the transfer
    Given I have completed the onboarding
    And the shopping list contains the items "Eggs" and "Butter"
    When I tap the shopping list button in the header
    And I check the item "Eggs"
    And I tap the "Move to pantry" button
    And I set "Eggs" to the category "Dairy", the quantity "12" "Units" and the expiration date "30/11/2026"
    And I tap the "Confirm" button
    Then I see "Butter" in the shopping list
    And I see "Eggs" in the pantry
    And I close the AUT
