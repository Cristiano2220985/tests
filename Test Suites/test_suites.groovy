import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW

CucumberKW.runFeatureFile('Include/features/OnboardingProfileCreationAndSwitching.feature')
CucumberKW.runFeatureFile('Include/features/DashboardFoodTrackingAndPantryItems.feature')

CucumberKW.runFeatureFolder('Include/features')