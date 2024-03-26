package com.example.grocerez.ui.myplate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.grocerez.ui.myplate.MyPlateModel
import kotlin.math.round


class MyPlateViewModel : ViewModel() {
    private var age: Int = 0
    private var weight: Double = 0.0
    private var height: Double = 0.0
    //private var sex: String = ""
    private var sex: Int = 0
    private var physicalActivityLevel: Int = 0
    val goals = mutableListOf<Goal>()

    data class Goal(
        val goalDescription: String,
        val calories: Int
    )

    private val _text = MutableLiveData<String>().apply {
        value = "GrocerEZ MyPlate Fragment"
    }
    val text: LiveData<String> = _text

    fun calculateBMI(weight: Double, height: Double): Double {
        // Convert height to meters
        val heightMeters = height * 0.0254
        // Convert weight to kilograms
        val weightKg = weight * 0.453592
        // Calculate BMI
        return round(weightKg / (heightMeters * heightMeters))
    }

    fun calculateCaloricExpenditure(bmr: Double, activityLevel: Int): Double {
        val activityFactors = listOf(1.2, 1.375, 1.55, 1.725, 1.9)
        return round(bmr * activityFactors[activityLevel])
    }

    fun calculateBMR(age: Int, weight: Double, height: Double, sex: Int): Double {
        val heightCm = height * 2.54
        // Convert weight to kilograms
        val weightKg = weight * 0.453592
        // BMR calculation based on Mifflin-St Jeor equation
        return when (sex) {
            0 -> round((9.99 * weightKg) + (6.25 * heightCm) - (4.92 * age) + 5) // Male
            1 -> round((9.99 * weightKg) + (6.25 * heightCm) - (4.92 * age) - 161) // Female
            else -> throw IllegalArgumentException("Invalid gender. Please select male or female.")
        }
    }

    fun determineMyPlateInfo(totalCaloricExpenditure: Double): String {
        return when (totalCaloricExpenditure.toInt()) {
            in 1600..1699 -> "myPlateInfoFor1600Cal"
            in 1700..1899 -> "myPlateInfoFor1800Cal"
            in 1900..2099 -> "myPlateInfoFor2000Cal"
            in 2100..2299 -> "myPlateInfoFor2200Cal"
            in 2300..2499 -> "myPlateInfoFor2400Cal"
            in 2500..2699 -> "myPlateInfoFor2600Cal"
            in 2700..2899 -> "myPlateInfoFor2800Cal"
            in 2900..3099 -> "myPlateInfoFor3000Cal"
            else -> "myPlateInfoFor3200Cal"
        }
    }
    fun updateUserData(age: Int, weight: Double, height: Double, sex: Int, physicalActivityLevel: Int) {
        this.age = age
        this.weight = weight
        this.height = height
        this.sex = sex
        this.physicalActivityLevel = physicalActivityLevel

        // Optionally, you can perform any additional logic or calculations here
        // Calculate BMI
        val bmi = calculateBMI(weight, height)
        val bmr = calculateBMR(age, weight, height, sex)

        // Calculate total caloric expenditure
        val totalCaloricExpenditure = calculateCaloricExpenditure(bmr, physicalActivityLevel)

        // Determine weight category
        when {
            bmi < 18.5 -> {
                // Underweight
                val calories = totalCaloricExpenditure+200
                val caloriesDescription = "To achieve a healthy weight:"
                val underweightGoal = Goal(caloriesDescription, calories.toInt())
                goals.add(underweightGoal)
                println("To achieve a healthy weight: ${totalCaloricExpenditure.toInt() + 200}")
                println("To achieve a healthy weight: ${calories.toInt()}")
                determineMyPlateInfo(totalCaloricExpenditure + 200)
            }
            bmi <= 24.9 -> {
                // Healthy weight
            }
            else -> {
                // Overweight
                val calories = totalCaloricExpenditure-200
                val caloriesDescription = "To achieve a healthy weight:"
                val overweightGoal = Goal(caloriesDescription, calories.toInt())
                goals.add(overweightGoal)
                println("to achieve a healthy weight: ${totalCaloricExpenditure.toInt() - 200}")
                val myPlateInfo = determineMyPlateInfo(totalCaloricExpenditure - 200)
                println("My Plate Info: $myPlateInfo")
            }
        }

        //CHAT HERE
        val myPlateInfo = determineMyPlateInfo(totalCaloricExpenditure)
        val calories = totalCaloricExpenditure
        val goalDescription = "Maintain current weight:"

        val maintainWeightGoal = Goal(goalDescription, calories.toInt())
        goals.add(maintainWeightGoal)

        // Display results
        println("\n***********************************************************")
        println("To maintain your current weight: ${totalCaloricExpenditure.toInt()}")
        println("My Plate Info: $myPlateInfo")
    }
}