package com.example.grocerez.ui.myplate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    data class MyPlateInfo(
        //val calorieRange: IntRange,
        val fruitAmount: Double,
        val vegetableAmount: Double,
        val grainAmount: Double,
        val proteinAmount: Double,
        val dairyAmount: Double
    )

    private val _text = MutableLiveData<String>().apply {
        value = "GrocerEZ MyPlate Fragment"
    }
    val text: LiveData<String> = _text

    // Define LiveData variables for the data you want to share
    private val _foodAmounts = MutableLiveData<MyPlateInfo>()
    val foodAmounts: LiveData<MyPlateInfo> = _foodAmounts

    fun updateFoodAmounts(foodAmounts: MyPlateInfo) {
        _foodAmounts.value = foodAmounts
    }

    fun calculateBMI(weight: Double, height: Double): Double {
        // Convert height to meters
        val heightMeters = height * 0.0254
        // Convert weight to kilograms
        //val weightKg = weight * 0.453592
        // Calculate BMI
        println("BMI WEIGHT:  $weight")
        return round(weight / (heightMeters * heightMeters))
    }

    fun calculateCaloricExpenditure(bmr: Double, activityLevel: Int): Double {
        val activityFactors = listOf(1.2, 1.375, 1.55, 1.725, 1.9)
        return round(bmr * activityFactors[activityLevel])
    }

    fun calculateBMR(age: Int, weight: Double, height: Double, sex: Int): Double {
        val heightCm = height * 2.54
        // Convert weight to kilograms
        //val weightKg = weight * 0.453592
        // BMR calculation based on Mifflin-St Jeor equation
        return when (sex) {
            0 -> round((9.99 * weight) + (6.25 * heightCm) - (4.92 * age) + 5) // Male
            1 -> round((9.99 * weight) + (6.25 * heightCm) - (4.92 * age) - 161) // Female
            else -> throw IllegalArgumentException("Invalid gender. Please select male or female.")
        }
    }

    fun determineMyPlateInfo(totalCaloricExpenditure: Double):MyPlateInfo {
        return when (totalCaloricExpenditure.toInt()) {
            in 1600..1699 -> MyPlateInfo(1.5, 2.0, 5.0, 5.0, 3.0)
            in 1700..1899 -> MyPlateInfo(1.5, 2.5, 6.0, 5.0, 3.0)
            in 1900..2099 -> MyPlateInfo(2.0, 2.5, 6.0, 5.5, 3.0)
            in 2100..2299 -> MyPlateInfo(2.0, 3.0, 7.0, 6.0, 3.0)
            in 2300..2499 -> MyPlateInfo(2.0, 3.0, 8.0, 6.5, 3.0)
            in 2500..2699 -> MyPlateInfo(2.0, 3.5, 9.0, 6.5, 3.0)
            in 2700..2899 -> MyPlateInfo(2.5, 3.5, 10.0, 7.0, 3.0)
            in 2900..3099 -> MyPlateInfo(2.5, 4.0, 10.0, 7.0, 3.0)
            else -> MyPlateInfo(2.5, 4.0, 10.0, 7.0, 3.0)

            // MyPlateInfo instances
//                val myPlateInfoFor1600Cal = MyPlateInfo(1.5, 2.0, 5.0, 5.0, 3.0)
//                val myPlateInfoFor1800Cal = MyPlateInfo(1.5, 2.5, 6.0, 5.0, 3.0)
//                val myPlateInfoFor2000Cal = MyPlateInfo(2.0, 2.5, 6.0, 5.5, 3.0)
//                val myPlateInfoFor2200Cal = MyPlateInfo(2.0, 3.0, 7.0, 6.0, 3.0)
//                val myPlateInfoFor2400Cal = MyPlateInfo(2.0, 3.0, 8.0, 6.5, 3.0)
//                val myPlateInfoFor2600Cal = MyPlateInfo(2.0, 3.5, 9.0, 6.5, 3.0)
//                val myPlateInfoFor2800Cal = MyPlateInfo(2.5, 3.5, 10.0, 7.0, 3.0)
//                val myPlateInfoFor3000Cal = MyPlateInfo(2.5, 4.0, 10.0, 7.0, 3.0)
//                val myPlateInfoFor3200Cal = MyPlateInfo(2.5, 4.0, 10.0, 7.0, 3.0)
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
//                val info = determineMyPlateInfo(totalCaloricExpenditure + 200)
//                println("Recommended servings:")
//                println("Fruit: ${info.fruitAmount}")
//                println("Vegetable: ${info.vegetableAmount}")
//                println("Grain: ${info.grainAmount}")
//                println("Protein: ${info.proteinAmount}")
//                println("Dairy: ${info.dairyAmount}")
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
//                val info = determineMyPlateInfo(totalCaloricExpenditure + 200)
//                println("Recommended servings:")
//                println("Fruit: ${info.fruitAmount}")
//                println("Vegetable: ${info.vegetableAmount}")
//                println("Grain: ${info.grainAmount}")
//                println("Protein: ${info.proteinAmount}")
//                println("Dairy: ${info.dairyAmount}")
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
//        println("My Plate Info: $myPlateInfo")
    }
}