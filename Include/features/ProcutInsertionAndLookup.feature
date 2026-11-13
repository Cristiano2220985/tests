Feature: Product insertion and lookup
  As a user
  I want to scan the barcode of a packaged product to fill in the item details
  So that I can add items to my pantry quickly, without typing everything by hand

  Scenario: Auto-fill the item form from a scanned barcode
    Given I have completed the onboarding
    And camera access is allowed
    When I tap the "+ Add item" button
    And I tap the "Scan barcode" button
    And I scan the barcode "3017620422003"
    Then I see "Nutella" in the "Item name" field
    And I see "400" in the "Quantity" field
    And I see "g" in the "Unit" field
    And I close the AUT

  Scenario: Adjust the auto-filled data before saving the item
    Given I have completed the onboarding
    And camera access is allowed
    When I tap the "+ Add item" button
    And I tap the "Scan barcode" button
    And I scan the barcode "3017620422003"
    And I insert "Chocolate spread" in the "Item name" field
    And I select the "Sweets" category
    And I set the expiration date to "30/11/2026"
    And I tap the "Save item" button
    Then I see "Chocolate spread" in the pantry
    And I close the AUT

  Scenario: Offer the fallback options when camera access is denied
    Given I have completed the onboarding
    And camera access is denied
    When I tap the "+ Add item" button
    And I tap the "Scan barcode" button
    Then I see the message "Camera access is not available"
    And I see the "Upload image" button
    And I see the "Enter manually" button
    And I close the AUT

  Scenario: Offer the fallback options when the barcode is not listed
    Given I have completed the onboarding
    And camera access is allowed
    When I tap the "+ Add item" button
    And I tap the "Scan barcode" button
    And I scan the barcode "0000000000000"
    Then I see the message "Product not found"
    And I see the "Upload image" button
    And I see the "Enter manually" button
    And I close the AUT

  Scenario: Auto-fill the item form from an uploaded barcode image
    Given I have completed the onboarding
    When I tap the "+ Add item" button
    And I tap the "Scan barcode" button
    And I tap the "Upload image" button
    And I upload the image "nutella-barcode.jpg"
    Then I see "Nutella" in the "Item name" field
    And I close the AUT

  Scenario: Enter the item manually after the fallback
    Given I have completed the onboarding
    And camera access is denied
    When I tap the "+ Add item" button
    And I tap the "Scan barcode" button
    And I tap the "Enter manually" button
    And I insert "Homemade jam" in the "Item name" field
    And I select the "Sweets" category
    And I insert "1" in the "Quantity" field
    And I select the "Units" unit
    And I set the expiration date to "30/11/2026"
    And I tap the "Save item" button
    Then I see "Homemade jam" in the pantry
    And I close the AUT