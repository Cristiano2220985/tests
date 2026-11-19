package stepDefinitions

import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * Glue code for the feature "Grocery shopping list".
 *
 * The elements are the test objects of "Object Repository/Page_GroceryShoppingList", all located
 * by data-testid. Objects that depend on an item receive its name as the "itemName" parameter.
 *
 * Shared steps defined in other step definitions:
 *  - US-01: "I insert {string} in the {string} field", "I tap the {string} button", "I close the AUT"
 *  - US-02: "I have completed the onboarding", "I see {string} in the pantry"
 *  - US-08: "camera access is allowed", "I scan the barcode {string}", "I see the message {string}"
 */
class GroceryShoppingListStepDefinition {

	private static final int TIMEOUT = 10
	private static final String CREATE_CATEGORY = '+ Create new category'

	// Given: set up the context ------------------------------------------------

	@Given("the shopping list is empty")
	def verifyShoppingListIsEmpty() {
		openShoppingList()
		WebUI.verifyElementVisible(object('div_Empty shopping list'))
		closeShoppingList()
	}

	@Given("the shopping list contains the item {string}")
	def addItemToShoppingList(String itemName) {
		addItemsToShoppingList([itemName])
	}

	@Given("the shopping list contains the items {string} and {string}")
	def addTwoItemsToShoppingList(String firstItem, String secondItem) {
		addItemsToShoppingList([firstItem, secondItem])
	}

	// When: perform the actions ------------------------------------------------

	@When("I tap the shopping list button in the header")
	def openShoppingList() {
		WebUI.click(object('button_Shopping list'))
		WebUI.waitForElementVisible(object('section_Shopping list'), TIMEOUT)
	}

	@When("I tap the scan button of the item {string}")
	def tapScanButton(String itemName) {
		WebUI.click(object('button_Item scan', itemName))
		WebUI.waitForElementVisible(object('div_Scanner'), TIMEOUT)
	}

	@When("I check the item {string}")
	def checkItem(String itemName) {
		WebUI.check(object('input_Item checkbox', itemName))
	}

	@When("I create the category {string} for {string}")
	def createCategory(String category, String itemName) {
		WebUI.selectOptionByLabel(object('select_Category', itemName), CREATE_CATEGORY, false)
		WebUI.setText(object('input_New category', itemName), category)
		WebUI.click(object('button_Add category', itemName))
		WebUI.verifyOptionSelectedByLabel(object('select_Category', itemName), category, false, TIMEOUT)
	}

	@When("I set {string} to the expiration date {string}")
	def setExpirationDate(String itemName, String date) {
		WebUI.setText(object('input_Expiration date', itemName), date.replace('/', ''))
	}

	@When("I set {string} to the category {string}, the quantity {string} {string} and the expiration date {string}")
	def setItemDetails(String itemName, String category, String quantity, String unit, String date) {
		WebUI.selectOptionByLabel(object('select_Category', itemName), category, false)
		WebUI.setText(object('input_Quantity', itemName), quantity)
		WebUI.selectOptionByLabel(object('select_Unit', itemName), unit, false)
		setExpirationDate(itemName, date)
	}

	// Then: check the outcome --------------------------------------------------

	@Then("I see the shopping list")
	def verifyShoppingListIsVisible() {
		WebUI.verifyElementVisible(object('section_Shopping list'))
	}

	@Then("I see {string} in the shopping list")
	def verifyItemInShoppingList(String itemName) {
		WebUI.verifyElementVisible(object('label_Item', itemName))
	}

	@Then("I do not see {string} in the shopping list")
	def verifyItemNotInShoppingList(String itemName) {
		WebUI.verifyElementNotPresent(object('label_Item', itemName), TIMEOUT)
	}

	@Then("I see the item {string} checked")
	def verifyItemChecked(String itemName) {
		WebUI.verifyElementChecked(object('input_Item checkbox', itemName), TIMEOUT)
	}

	@Then("I see the quantity {string} {string} filled in for {string}")
	def verifyFilledQuantity(String quantity, String unit, String itemName) {
		WebUI.waitForElementVisible(object('section_Move to pantry'), TIMEOUT)
		WebUI.verifyElementAttributeValue(object('input_Quantity', itemName), 'value', quantity, TIMEOUT)
		WebUI.verifyOptionSelectedByLabel(object('select_Unit', itemName), unit, false, TIMEOUT)
	}

	@Then("I see the {string} category in the pantry filters")
	def verifyCategoryFilter(String category) {
		List<String> filters = WebUI.findWebElements(object('button_Pantry category filter'), TIMEOUT)*.getText()
		WebUI.verifyEqual(filters.contains(category), true)
	}

	// Helpers ------------------------------------------------------------------

	/** Adds the items through the shopping list and closes it again. */
	private void addItemsToShoppingList(List<String> itemNames) {
		openShoppingList()
		itemNames.each { itemName ->
			WebUI.setText(object('input_Item name'), itemName)
			WebUI.click(object('button_Add'))
			verifyItemInShoppingList(itemName)
		}
		closeShoppingList()
	}

	private void closeShoppingList() {
		WebUI.click(object('button_Close shopping list'))
		WebUI.waitForElementNotPresent(object('section_Shopping list'), TIMEOUT)
	}

	/** Returns a test object of Page_GroceryShoppingList, filled in with the item name when needed. */
	private TestObject object(String name, String itemName = null) {
		return findTestObject("Page_GroceryShoppingList/${name}", [('itemName') : itemName])
	}
}
