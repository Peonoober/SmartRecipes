package com.example.smartrecipes.ui

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.example.smartrecipes.R
import com.example.smartrecipes.databinding.ActivityIngredientsBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class IngredientsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIngredientsBinding
    private val maxSelected = 10

    // Категории и ингредиенты (100+ штук)
    private val categories: LinkedHashMap<String, List<String>> = linkedMapOf(
        "Мясо и птица" to listOf(
            "Курица",
            "Говядина",
            "Свинина",
            "Индейка",
            "Баранина",
            "Фарш говяжий",
            "Фарш свиной",
            "Фарш куриный",
            "Ветчина",
            "Бекон",
            "Колбаса",
            "Сосиски",
            "Котлеты готовые",
            "Печень куриная",
            "Печень говяжья"
        ),

        "Рыба и морепродукты" to listOf(
            "Рыба белая",
            "Лосось",
            "Форель",
            "Тунец",
            "Скумбрия",
            "Минтай",
            "Креветки",
            "Кальмары",
            "Мидии",
            "Крабовые палочки",
            "Икра красная",
            "Икра чёрная"
        ),

        "Овощи" to listOf(
            "Картофель",
            "Морковь",
            "Лук репчатый",
            "Чеснок",
            "Свекла",
            "Капуста белокочанная",
            "Капуста цветная",
            "Брокколи",
            "Перец болгарский",
            "Огурцы свежие",
            "Помидоры свежие",
            "Кабачок",
            "Баклажан",
            "Зелёный горошек",
            "Кукуруза консервированная",
            "Шпинат",
            "Сельдерей стеблевой",
            "Редис",
            "Тыква"
        ),

        "Зелень и специи" to listOf(
            "Укроп",
            "Петрушка",
            "Зелёный лук",
            "Кинза",
            "Базилик",
            "Орегано",
            "Тимьян",
            "Розмарин",
            "Лавровый лист",
            "Паприка молотая",
            "Перец чёрный молотый",
            "Перец красный молотый",
            "Карри",
            "Куркума",
            "Соль",
            "Сахар",
            "Ванильный сахар",
            "Чеснок сушёный",
            "Хмели-сунели"
        ),

        "Молочные продукты" to listOf(
            "Молоко",
            "Сливки",
            "Сметана",
            "Йогурт натуральный",
            "Творог",
            "Сыр твёрдый",
            "Сыр полутвёрдый",
            "Сыр мягкий",
            "Сыр плавленый",
            "Сыр фета",
            "Сыр моцарелла",
            "Рикотта",
            "Масло сливочное",
            "Сгущённое молоко",
            "Кефир"
        ),

        "Яйца и выпечка" to listOf(
            "Яйца куриные",
            "Мука пшеничная",
            "Мука цельнозерновая",
            "Мука кукурузная",
            "Дрожжи сухие",
            "Дрожжи свежие",
            "Сода пищевая",
            "Разрыхлитель",
            "Крахмал картофельный",
            "Крахмал кукурузный",
            "Сахарная пудра",
            "Какао-порошок",
            "Ванилин",
            "Мёд",
            "Размягчённое сливочное масло"
        ),

        "Крупы и макароны" to listOf(
            "Рис",
            "Гречка",
            "Овсяные хлопья",
            "Манка",
            "Перловка",
            "Булгур",
            "Киноа",
            "Пшено",
            "Макароны",
            "Спагетти",
            "Лапша яичная",
            "Рисовая лапша",
            "Кускус",
            "Удон"
        ),

        "Бобовые" to listOf(
            "Фасоль красная",
            "Фасоль белая",
            "Нут",
            "Чечевица красная",
            "Чечевица зелёная",
            "Горох колотый",
            "Соевые бобы",
            "Стручковая фасоль"
        ),

        "Фрукты и ягоды" to listOf(
            "Яблоки",
            "Груши",
            "Бананы",
            "Апельсины",
            "Мандарины",
            "Лимон",
            "Лайм",
            "Персики",
            "Нектарины",
            "Виноград",
            "Клубника",
            "Малина",
            "Черника",
            "Смородина",
            "Киви",
            "Ананас"
        ),

        "Масла, соусы и консервы" to listOf(
            "Масло подсолнечное",
            "Масло оливковое",
            "Масло сливочное топлёное",
            "Масло кунжутное",
            "Майонез",
            "Кетчуп",
            "Соевый соус",
            "Томатная паста",
            "Горчица",
            "Уксус столовый",
            "Уксус яблочный",
            "Уксус бальзамический",
            "Томаты в собственном соку",
            "Оливки",
            "Маслины"
        ),

        "Орехи и семена" to listOf(
            "Грецкие орехи",
            "Миндаль",
            "Арахис",
            "Фундук",
            "Кешью",
            "Фисташки",
            "Семечки подсолнечные",
            "Семена льна",
            "Семена чиа",
            "Тыквенные семечки"
        ),

        "Хлеб и прочее" to listOf(
            "Хлеб белый",
            "Хлеб чёрный",
            "Батон",
            "Лаваш",
            "Пита",
            "Панировочные сухари",
            "Сухари",
            "Вода",
            "Желатин",
            "Маргарин"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIngredientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        populateChips()
        updateCounter()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnDone.setOnClickListener {
            val selected = getSelectedIngredients()
            if (selected.isEmpty()) {
                Toast.makeText(this, "Выберите хотя бы один ингредиент", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            SearchFilters.selectedIngredients = selected

            Toast.makeText(
                this,
                "Выбрано: ${selected.joinToString()}",
                Toast.LENGTH_LONG
            ).show()

            finish()
        }
    }

    private fun populateChips() {
        val container = binding.containerIngredients
        container.removeAllViews()

        val headerColor = ContextCompat.getColor(this, R.color.text_primary)
        val chipTextColor = ContextCompat.getColor(this, R.color.text_primary)
        val chipTextColorSelected = ContextCompat.getColor(this, R.color.white)

        for ((category, items) in categories) {

            // Заголовок категории (обычный TextView)
            val label = TextView(this).apply {
                text = category
                setTextColor(headerColor)
                textSize = 16f
                setPadding(0, 16, 0, 4)
            }
            container.addView(label)

            // ChipGroup для ингредиентов этой категории
            val chipGroup = ChipGroup(this).apply {
                isSingleSelection = false
                isClickable = true
                isFocusable = false
                chipSpacingHorizontal = 8
                chipSpacingVertical = 8
            }

            for (item in items) {
                val chip = Chip(this).apply {
                    text = item
                    isCheckable = true
                    isClickable = true
                    isCheckedIconVisible = false

                    setChipBackgroundColorResource(R.color.chip_bg)
                    setTextColor(chipTextColor)

                    setOnCheckedChangeListener { buttonView, isChecked ->
                        val currentSelected = getSelectedIngredients().size

                        if (isChecked && currentSelected > maxSelected) {
                            buttonView.isChecked = false
                            Toast.makeText(
                                this@IngredientsActivity,
                                "Можно выбрать не больше $maxSelected ингредиентов",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnCheckedChangeListener
                        }

                        if (isChecked) {
                            setChipBackgroundColorResource(R.color.primary)
                            setTextColor(chipTextColorSelected)
                        } else {
                            setChipBackgroundColorResource(R.color.chip_bg)
                            setTextColor(chipTextColor)
                        }

                        updateCounter()
                    }
                }

                chipGroup.addView(chip)
            }

            container.addView(chipGroup)
        }
    }

    private fun getSelectedIngredients(): List<String> {
        val list = mutableListOf<String>()

        // Проходим по всем дочерним элементам containerIngredients,
        // находим ChipGroup'ы и внутри них — отмеченные Chip'ы
        for (view in binding.containerIngredients.children) {
            if (view is ChipGroup) {
                for (chipView in view.children) {
                    val chip = chipView as? Chip ?: continue
                    if (chip.isChecked) {
                        list.add(chip.text.toString())
                    }
                }
            }
        }
        return list
    }

    private fun updateCounter() {
        val count = getSelectedIngredients().size
        binding.tvCounter.text = "$count/$maxSelected"
    }
}