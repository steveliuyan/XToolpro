package com.steveliuyan.xtoolpro

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShellNavigationTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun selectingToolboxShowsToolboxContent() {
        composeRule.onNodeWithContentDescription(text(R.string.nav_toolbox)).performClick()

        composeRule.onNodeWithText(text(R.string.toolbox_subtitle)).assertIsDisplayed()
    }

    @Test
    fun selectingTasksShowsEmptyTaskState() {
        composeRule.onNodeWithContentDescription(text(R.string.nav_tasks)).performClick()

        composeRule.onNodeWithText(text(R.string.tasks_empty_title)).assertIsDisplayed()
    }

    @Test
    fun selectingSettingsShowsSettingsContent() {
        composeRule.onNodeWithContentDescription(text(R.string.nav_settings)).performClick()

        composeRule.onNodeWithText(text(R.string.settings_subtitle)).assertIsDisplayed()
    }

    private fun text(resourceId: Int): String = composeRule.activity.getString(resourceId)
}
