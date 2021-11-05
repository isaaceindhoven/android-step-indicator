package nl.isaac.android.stepindicator.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import nl.isaac.android.stepindicator.StepIndicator

class MainActivity : AppCompatActivity() {

    private val viewPager2: ViewPager2 by lazy {
        findViewById(R.id.mainViewPager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pagerAdapter = ViewPager2Adapter(this)
        viewPager2.adapter = pagerAdapter

        setupStepIndicatorNumbers()
        setupStepIndicatorIcons()
        setupStepIndicatorMixed()
    }

    private fun setupStepIndicatorNumbers() {
        val stepIndicatorNumbers: StepIndicator = findViewById(R.id.stepIndicatorNumbers)
        stepIndicatorNumbers.setupWithViewPager(viewPager2)
    }

    private fun setupStepIndicatorIcons() {
        val stepIndicatorIcons: StepIndicator = findViewById(R.id.stepIndicatorIcons)
        stepIndicatorIcons.apply {
            setupWithViewPager(viewPager2)
            stepType = StepIndicator.StepType.Icon(
                listOf(
                    R.drawable.ic_shopping_cart,
                    R.drawable.ic_location,
                    R.drawable.ic_credit_card,
                    R.drawable.ic_track
                )
            )
            showLabels = true
            labels = listOf("Shopping cart", "Address", "Payment method", "Track")
            fillNextStep = false
            fillPreviousStep = true
            previousStepColor = ContextCompat.getColor(context, R.color.dark_blue)
            activeStepColor = ContextCompat.getColor(context, R.color.dark_blue)
            previousStepLabelColor = ContextCompat.getColor(context, R.color.dark_blue)
            activeStepLabelColor = ContextCompat.getColor(context, R.color.dark_blue)
            activeStepIndicatorTypeColor = ContextCompat.getColor(context, R.color.dark_blue)
            previousStepIndicatorTypeColor = ContextCompat.getColor(context, R.color.white)
            nextStepIndicatorTypeColor = ContextCompat.getColor(context, R.color.dark_blue)
        }
    }

    private fun setupStepIndicatorMixed() {
        val stepIndicatorMixed: StepIndicator = findViewById(R.id.stepIndicatorMixed)
        stepIndicatorMixed.apply {
            setupWithViewPager(viewPager2)
            stepType = StepIndicator.StepType.Mixed(
                listOf(
                    Pair(StepIndicator.StepType.StepIndicatorTypeMixedOption.NONE, ""),
                    Pair(StepIndicator.StepType.StepIndicatorTypeMixedOption.ICON, R.drawable.ic_airplane_ticket),
                    Pair(StepIndicator.StepType.StepIndicatorTypeMixedOption.TEXT, "XH1"),
                    Pair(StepIndicator.StepType.StepIndicatorTypeMixedOption.ICON, R.drawable.ic_flight_takeoff)
                )
            )
            showLabels = true
            labels = listOf("Arrive", "Check in", "Depart", "Fly")
        }
    }
}
