Feature: GenAI Sous-Chef

As a user
I want the AI to create recipes from available ingredients
So that I can use leftovers and discover new meals

Scenario: Generate a recipe from leftovers

Given I have completed the onboarding
And the pantry contains "Rice", "Chicken" and "Carrot"
When I open GenAI Sous-Chef
And I select those ingredients
And I tap "Generate Recipe"
Then I receive a generated recipe
And I close the AUT

Scenario: Apply dietary restrictions to generated recipes

Given the active profile is "Vegetarian"
When I generate a recipe
Then the generated recipe does not contain meat ingredients
And I close the AUT

Scenario: Save an AI recipe

Given an AI recipe was generated
When I tap "Save Recipe"
Then the recipe appears in the recipe book
And I close the AUT