Feature: Onboarding, profile creation and switching
  As a user
  I want to set up my household profiles and switch between them
  So that recipes and suggestions adapt to the dietary needs and servings of whoever is eating

  Scenario: Set up the default profile on first launch
    Given I start the application for the first time
    When I tap the "Get started" button
    And I insert "Family" in the "Profile name" field
    And I set the typical servings to 4
    And I tap the "Next" button
    And I select the "Vegetarian" dietary lifestyle
    And I tap the "Next" button
    And I select the "Peanuts" allergy
    And I add "Mushrooms" to the excluded ingredients
    And I tap the "Finish setup" button
    Then I see the dashboard
    And I see "Family" as the active profile
    And I close the AUT

Scenario: Create a new profile from the dashboard
    Given I have completed the onboarding with the profile "Family"
    When I tap the profile switcher in the header
    And I tap the "+ Add new profile" button
    And I complete the setup for the profile "Ana"
    And I tap the profile switcher in the header
    Then I see "Ana" in the list of profiles
    And I close the AUT

  Scenario: Switch the active profile from the header
    Given I have the profiles "Family" and "Ana"
    And I have the profile "Family" active
    When I tap the profile switcher in the header
    And I choose the profile "Ana"
    Then I see "Ana" as the active profile
    And I close the AUT