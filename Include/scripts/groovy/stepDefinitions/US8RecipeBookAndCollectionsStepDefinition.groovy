package stepDefinitions

import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import org.openqa.selenium.Keys

import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * Glue code for the feature "Recipe Book" (US-8, formerly US-24).
 *
 * The elements are the test objects of "Object Repository/Page_RecipeBook", all located by
 * data-testid. Objects that depend on a recipe or a collection receive its name as the
 * "recipeName" or "collectionName" parameter.
 *
 * The recipes come from TheMealDB (recipe discovery, US-19): the step "I am viewing the recipe"
 * adds one of the recipe's ingredients to the pantry and searches for recipes.
 *
 * The scenario remembers the recipe and the collection named in the steps, so steps such as
 * "I add the recipe to the collection" know which ones to use.
 *
 * Shared steps defined in other step definitions:
 *  - US-01: "I tap the {string} button" (used for "Save Recipe"), "I close the AUT"
 *  - US-02: "I have completed the onboarding"
 */
class US8RecipeBookAndCollectionsStepDefinition {

	private static final int TIMEOUT = 10
	private static final int SEARCH_TIMEOUT = 30 // the recipe search calls TheMealDB

	/** Pantry ingredient used to find each recipe in recipe discovery. */
	private static final Map<String, String> RECIPE_INGREDIENTS = [
		'Fettuccine Alfredo': 'Fettuccine',
	]

	private String recipeName
	private String collectionName

	// Given: set up the context ------------------------------------------------

	@Given("I am viewing the recipe {string}")
	def viewRecipe(String recipe) {
		recipeName = recipe
		WebUI.click(object('a_Nav pantry'))
		new DashboardFoodTrackingAndPantryItemsStepDefinition().addItemWithQuantity(RECIPE_INGREDIENTS[recipe], '1')
		WebUI.click(object('a_Nav recipes'))
		WebUI.click(object('button_Recipes search'))
		WebUI.waitForElementClickable(object('button_Recipe card title'), SEARCH_TIMEOUT)
		WebUI.click(object('button_Recipe card title'))
		WebUI.waitForElementVisible(object('section_Recipe detail'), TIMEOUT)
	}

	@Given("the collection {string} exists")
	def createCollectionAfterOnboarding(String collection) {
		new DashboardFoodTrackingAndPantryItemsStepDefinition().completeOnboarding()
		openRecipeBook()
		createCollection(collection)
		verifyCollection(collection)
	}

	@Given("the recipe {string} is saved")
	def saveRecipe(String recipe) {
		viewRecipe(recipe)
		new OnboardingProfileCreationAndSwitchingStepDefinition().tapButton('Save Recipe')
		verifyRecipeInBook(recipe)
	}

	@Given("the recipe {string} belongs to the collection {string}")
	def addRecipeToNewCollection(String recipe, String collection) {
		createCollectionAfterOnboarding(collection)
		saveRecipe(recipe)
		addRecipeToCollection()
		verifyRecipeInCollection(recipe, collection)
	}

	// When: perform the actions ------------------------------------------------

	@When("I open the recipe book")
	def openRecipeBook() {
		closeRecipeDetail()
		WebUI.click(object('a_Nav book'))
		WebUI.waitForElementVisible(object('div_Book view'), TIMEOUT)
	}

	@When("I create a collection named {string}")
	def createCollection(String collection) {
		collectionName = collection
		WebUI.click(object('button_New collection'))
		WebUI.waitForElementVisible(object('section_Create collection'), TIMEOUT)
		WebUI.setText(object('input_Collection name'), collection)
		WebUI.click(object('button_Create collection'))
		WebUI.waitForElementNotPresent(object('section_Create collection'), TIMEOUT)
	}

	@When("I add the recipe to the collection")
	def addRecipeToCollection() {
		openRecipeBook()
		WebUI.click(object('button_Add to collection'))
		WebUI.waitForElementVisible(object('ul_Collection picker'), TIMEOUT)
		WebUI.click(object('button_Collection option'))
		WebUI.waitForElementNotPresent(object('ul_Collection picker'), TIMEOUT)
	}

	@When("I remove the recipe from the collection")
	def removeRecipeFromCollection() {
		openCollection(collectionName)
		WebUI.click(object('button_Remove from collection'))
	}

	// Then: check the outcome --------------------------------------------------

	@Then("{string} appears in my recipe book")
	def verifyRecipeInBook(String recipe) {
		openRecipeBook()
		WebUI.verifyElementVisible(object('article_Saved recipe', recipe))
	}

	@Then("I see the collection {string}")
	def verifyCollection(String collection) {
		WebUI.verifyElementVisible(object('button_Collection card', null, collection))
		WebUI.verifyEqual(WebUI.getText(object('span_Collection name', null, collection)).trim(), collection)
	}

	@Then("{string} appears in {string}")
	def verifyRecipeInCollection(String recipe, String collection) {
		openCollection(collection)
		WebUI.verifyElementVisible(object('article_Collection recipe', recipe))
	}

	@Then("I do not see {string} in {string}")
	def verifyRecipeNotInCollection(String recipe, String collection) {
		openCollection(collection)
		WebUI.verifyElementNotPresent(object('article_Collection recipe', recipe), TIMEOUT)
	}

	// Helpers ------------------------------------------------------------------

	private void openCollection(String collection) {
		WebUI.click(object('button_Collection card', null, collection))
		WebUI.waitForElementVisible(object('section_Collection detail', null, collection), TIMEOUT)
	}

	/** The recipe details cover the navigation, so they are closed (Escape) when open. */
	private void closeRecipeDetail() {
		if (!recipeName) {
			return
		}
		TestObject detail = object('section_Recipe detail')
		if (WebUI.waitForElementPresent(detail, 1)) {
			WebUI.sendKeys(detail, Keys.chord(Keys.ESCAPE))
			WebUI.waitForElementNotPresent(detail, TIMEOUT)
		}
	}

	/**
	 * Returns a test object of Page_RecipeBook. By default it uses the recipe and the collection
	 * remembered by the scenario.
	 */
	private TestObject object(String name, String recipe = recipeName, String collection = collectionName) {
		return findTestObject("Page_RecipeBook/${name}", [('recipeName') : recipe, ('collectionName') : collection])
	}
}
