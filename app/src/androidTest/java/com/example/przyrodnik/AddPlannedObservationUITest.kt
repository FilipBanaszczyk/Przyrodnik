package com.example.przyrodnik


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AddPlannedObservationUITest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainMenuActivity::class.java)

    @Test
    fun addPlannedObservationUITest() {
        val bottomNavigationItemView = onView(
                allOf(withId(R.id.page_3), withContentDescription("Planuj"),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withId(R.id.bottomNavigationView),
                                                childAtPosition(
                                                        childAtPosition(
                                                                allOf(withId(android.R.id.content),
                                                                        childAtPosition(
                                                                                allOf(withId(R.id.action_bar_root),
                                                                                        childAtPosition(
                                                                                                childAtPosition(
                                                                                                        withClassName(`is`("android.widget.LinearLayout")),
                                                                                                        1),
                                                                                                0)),
                                                                                1)),
                                                                0),
                                                        0)),
                                        0),
                                2),
                        isDisplayed()))
        bottomNavigationItemView.perform(click())

        val floatingActionButton = onView(
                allOf(withId(R.id.fab),
                        childAtPosition(
                                allOf(withId(R.id.lin),
                                        childAtPosition(
                                                allOf(withId(android.R.id.content),
                                                        childAtPosition(
                                                                allOf(withId(R.id.action_bar_root),
                                                                        childAtPosition(
                                                                                childAtPosition(
                                                                                        withClassName(`is`("android.widget.LinearLayout")),
                                                                                        1),
                                                                                0)),
                                                                1)),
                                                0)),
                                1),
                        isDisplayed()))
        floatingActionButton.perform(click())

        val editText = onView(
                allOf(childAtPosition(
                        allOf(withId(android.R.id.custom),
                                childAtPosition(
                                        allOf(withClassName(`is`("android.widget.FrameLayout")),
                                                childAtPosition(
                                                        allOf(withClassName(`is`("com.android.internal.widget.AlertDialogLayout")),
                                                                childAtPosition(
                                                                        allOf(withId(android.R.id.content),
                                                                                childAtPosition(
                                                                                        withClassName(`is`("android.widget.FrameLayout")),
                                                                                        0)),
                                                                        0)),
                                                        2)),
                                        0)),
                        0),
                        isDisplayed()))
        editText.perform(replaceText("obserwacja ptaków"), closeSoftKeyboard())

        val appCompatButton = onView(
                allOf(withId(android.R.id.button1), withText("Zapisz"),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withClassName(`is`("android.widget.ScrollView")),
                                                childAtPosition(
                                                        allOf(withClassName(`is`("com.android.internal.widget.AlertDialogLayout")),
                                                                childAtPosition(
                                                                        allOf(withId(android.R.id.content),
                                                                                childAtPosition(
                                                                                        withClassName(`is`("android.widget.FrameLayout")),
                                                                                        0)),
                                                                        0)),
                                                        3)),
                                        0),
                                3)))
        appCompatButton.perform(scrollTo(), click())

        val appCompatButton2 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withClassName(`is`("android.widget.ScrollView")),
                                                childAtPosition(
                                                        allOf(withClassName(`is`("com.android.internal.widget.AlertDialogLayout")),
                                                                childAtPosition(
                                                                        allOf(withId(android.R.id.content),
                                                                                childAtPosition(
                                                                                        withClassName(`is`("android.widget.FrameLayout")),
                                                                                        0)),
                                                                        0)),
                                                        3)),
                                        0),
                                3)))
        appCompatButton2.perform(scrollTo(), click())

        val appCompatButton3 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withClassName(`is`("android.widget.ScrollView")),
                                                childAtPosition(
                                                        childAtPosition(
                                                                childAtPosition(
                                                                        allOf(withClassName(`is`("android.widget.TimePicker")),
                                                                                childAtPosition(
                                                                                        allOf(withId(android.R.id.custom),
                                                                                                childAtPosition(
                                                                                                        allOf(withClassName(`is`("android.widget.FrameLayout")),
                                                                                                                childAtPosition(
                                                                                                                        allOf(withClassName(`is`("com.android.internal.widget.AlertDialogLayout")),
                                                                                                                                childAtPosition(
                                                                                                                                        withId(android.R.id.content),
                                                                                                                                        0)),
                                                                                                                        2)),
                                                                                                        0)),
                                                                                        0)),
                                                                        0),
                                                                4),
                                                        2)),
                                        0),
                                3)))
        appCompatButton3.perform(scrollTo(), click())

        val textView = onView(
                allOf(withId(R.id.plan_content), withText("obserwacja ptaków"),
                        withParent(allOf(withId(R.id.plan_contents),
                                withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java)))),
                        isDisplayed()))
        textView.check(matches(withText("obserwacja ptaków")))

        val textView2 = onView(
                allOf(withId(R.id.plan_time), withText("11-01-2021 21:58"),
                        withParent(allOf(withId(R.id.plan_contents),
                                withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java)))),
                        isDisplayed()))
        textView2.check(matches(withText("11-01-2021 21:58")))
    }

    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
