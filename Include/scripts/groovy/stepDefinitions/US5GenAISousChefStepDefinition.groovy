package stepDefinitions

import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import java.util.regex.Pattern

import org.openqa.selenium.By
import org.openqa.selenium.WebElement

import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

class US5GenAISousChefStepDefinition {

	private static final int TIMEOUT = 10
	private static final int GENERATION_TIMEOUT = 40
	private static final Map<String, String> BUTTONS = [
		'Generate Recipe': 'button_Generate recipe',
		'Save Recipe': 'button_Save recipe',
	]
	private static final Pattern MEAT_INGREDIENT = ~/(?ix)\b(
		chickens?|poultry|turkeys?|ducks?|goose|beef|veal|pork|bacon|ham|lamb|mutton|
		venison|rabbit|lard|sausages?|salami|chorizo|pepperoni|meatballs?|meat|
		fish|seafood|shellfish|salmon|tuna|cod|anchov(?:y|ies)|sardines?|trout|
		shrimps?|prawns?|crabs?|lobsters?|mussels?|oysters?|squid|octopus
	)\b/

	private final RecipeDiscoveryandPantryOverlapStepDefinition recipes = new RecipeDiscoveryandPantryOverlapStepDefinition()
	private List<String> pantryIngredients = []
	private String generatedRecipeName

	@Given("the pantry contains {string}, {string} and {string}")
	def pantryContainsThree(String first, String second, String third) {
		addPantryIngredients([first, second, third])
	}

	@When("I open GenAI Sous-Chef")
	def openSousChef() {
		recipes.openRecipeDiscoveryPage()
		click(object('button_Nav Sous Chef'))
		assert WebUI.waitForElementVisible(object('div_GenAI Sous-Chef'), TIMEOUT) : 'GenAI Sous-Chef did not open'
	}

	@When("I select those ingredients")
	def selectPantryIngredients() {
		assert WebUI.waitForElementVisible(object('ul_Pantry ingredients'), TIMEOUT) : 'The pantry ingredient list is not visible'
		pantryIngredients.each { String ingredient ->
			TestObject option = object('button_Ingredient option', [ingredientName: ingredient])
			click(option)
			WebUI.verifyElementAttributeValue(option, 'aria-pressed', 'true', TIMEOUT)
		}
	}

	@When("I tap {string}")
	def tapButton(String button) {
		assert BUTTONS.containsKey(button) : "Unknown GenAI Sous-Chef button: '${button}'"
		click(object(BUTTONS[button]))
	}

	@Then("I receive a generated recipe")
	def verifyGeneratedRecipe() {
		waitForGeneratedRecipe()
		generatedIngredients()
		WebElement steps = WebUI.findWebElement(object('ol_Generated steps'), TIMEOUT)
		assert steps.isDisplayed() && !steps.findElements(By.tagName('li')).isEmpty() : 'The recipe has no visible preparation steps'
	}

	@When("I generate a recipe")
	def generateVegetarianRecipe() {
		// Offer a meat ingredient so the restriction is exercised with a conflicting selection.
		generateFromPantry(['Chicken', 'Carrot'])
	}

	@Then("the generated recipe does not contain meat ingredients")
	def verifyNoMeatIngredients() {
		waitForGeneratedRecipe()
		List<String> meat = generatedIngredients().findAll { MEAT_INGREDIENT.matcher(it).find() }
		assert meat.isEmpty() : "The generated recipe contains meat or seafood: ${meat}"
	}

	@Given("an AI recipe was generated")
	def generateRecipeForSaving() {
		new DashboardFoodTrackingAndPantryItemsStepDefinition().completeOnboarding()
		generateFromPantry(['Rice', 'Carrot'])
		verifyGeneratedRecipe()
	}

	@Then("the recipe appears in the recipe book")
	def verifyRecipeInBook() {
		WebElement link = WebUI.findWebElements(object('a_Nav book'), TIMEOUT).find { it.isDisplayed() }
		assert link != null : 'The Recipe Book navigation link is not visible'
		link.click()
		assert WebUI.waitForElementVisible(object('div_Book view'), TIMEOUT) : 'The Recipe Book did not open'
		WebUI.verifyElementVisible(object('ul_Saved recipes'))
		Map variables = [recipeName: escapeCssString(generatedRecipeName)]
		TestObject card = object('article_Saved recipe', variables)
		assert WebUI.waitForElementVisible(card, TIMEOUT) : "The generated recipe '${generatedRecipeName}' is missing from the book"
		WebUI.verifyEqual(WebUI.getText(object('button_Saved recipe title', variables)).trim(), generatedRecipeName)
	}

	private void addPantryIngredients(List<String> ingredients) {
		pantryIngredients = ingredients
		ingredients.each { recipes.pantryContains(it) }
	}

	private void generateFromPantry(List<String> ingredients) {
		addPantryIngredients(ingredients)
		openSousChef()
		selectPantryIngredients()
		tapButton('Generate Recipe')
	}

	private void waitForGeneratedRecipe() {
		assert WebUI.waitForElementVisible(object('section_Generated recipe'), GENERATION_TIMEOUT) : 'GenAI Sous-Chef did not display a generated recipe'
		generatedRecipeName = WebUI.getText(object('h2_Generated recipe title')).trim()
		assert generatedRecipeName : 'The generated recipe has no title'
	}

	private List<String> generatedIngredients() {
		WebUI.verifyElementVisible(object('ul_Generated ingredients'))
		List<String> names = WebUI.findWebElements(object('li_Generated ingredient'), TIMEOUT)
				.findAll { it.isDisplayed() }
				.collect { it.getAttribute('data-ingredient-name') }
		assert names && names.every { it?.trim() } : 'The generated recipe has missing ingredient names'
		return names
	}

	private void click(TestObject target) {
		assert WebUI.waitForElementClickable(target, TIMEOUT) : "The target '${target.objectId}' is not clickable"
		WebUI.click(target)
	}

	private static String escapeCssString(String value) {
		return value.replace('\\', '\\\\').replace('"', '\\"')
	}

	private TestObject object(String name, Map variables = [:]) {
		return findTestObject("Page_GenAISousChef/${name}", variables)
	}
}
