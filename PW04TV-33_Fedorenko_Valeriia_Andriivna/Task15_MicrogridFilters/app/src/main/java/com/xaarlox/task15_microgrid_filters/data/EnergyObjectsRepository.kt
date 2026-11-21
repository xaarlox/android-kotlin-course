package com.xaarlox.task15_microgrid_filters.data

import com.xaarlox.task15_microgrid_filters.domain.EnergyObject
import com.xaarlox.task15_microgrid_filters.domain.EnergyType
import com.xaarlox.task15_microgrid_filters.domain.ObjectStatus
import java.time.LocalDateTime

/**
 * A simulation of the data of a local microgrid with renewable sources (the Kyiv area).
 * In a real app this would be a database or a network request —
 * the rest of the code works only with a collection
 */
object EnergyObjectsRepository {

    private fun at(day: Int, hour: Int, minute: Int) = LocalDateTime.of(2026, 11, day, hour, minute)

    val objects: List<EnergyObject> = listOf(
        EnergyObject(
            "solar-north", "СЕС «Оболонь»", EnergyType.SOLAR, ObjectStatus.WORKING,
            "Наземна сонячна станція з трекерами; основне джерело генерації вдень.",
            50.5200, 30.4900, 120.0, 78, at(21, 13, 5)
        ),
        EnergyObject(
            "solar-roof", "Дахова СЕС «Теплиця»", EnergyType.SOLAR, ObjectStatus.WORKING,
            "Сонячні панелі на даху тепличного комплексу.",
            50.4300, 30.6000, 60.0, 64, at(21, 12, 48)
        ),
        EnergyObject(
            "solar-field", "СЕС «Поділ»", EnergyType.SOLAR, ObjectStatus.STANDBY,
            "Резервна польова станція, вмикається за дефіциту генерації.",
            50.5000, 30.4300, 200.0, 0, at(20, 18, 20)
        ),
        EnergyObject(
            "solar-forest", "СЕС «Лісова»", EnergyType.SOLAR, ObjectStatus.MAINTENANCE,
            "Планове очищення панелей та заміна інверторів.",
            50.4800, 30.3900, 40.0, 0, at(19, 9, 30)
        ),
        EnergyObject(
            "wind-1", "Вітрогенератор «Вітер-1»", EnergyType.WIND, ObjectStatus.WORKING,
            "Горизонтально-осьова турбіна, працює вночі та за хмарної погоди.",
            50.4000, 30.4200, 80.0, 71, at(21, 13, 2)
        ),
        EnergyObject(
            "wind-2", "Вітрогенератор «Вітер-2»", EnergyType.WIND, ObjectStatus.WORKING,
            "Друга турбіна на західній ділянці з найстабільнішим вітром.",
            50.4700, 30.3600, 80.0, 55, at(21, 12, 59)
        ),
        EnergyObject(
            "wind-3", "Вітрогенератор «Вітер-3»", EnergyType.WIND, ObjectStatus.FAULT,
            "Аварійна зупинка: перегрів підшипника, потрібен виїзд бригади.",
            50.4200, 30.3700, 120.0, 0, at(21, 7, 41)
        ),
        EnergyObject(
            "wind-4", "Вітрогенератор «Вітер-4»", EnergyType.WIND, ObjectStatus.STANDBY,
            "Мала турбіна, у резерві через слабкий вітер.",
            50.4600, 30.6200, 50.0, 0, at(20, 22, 10)
        ),
        EnergyObject(
            "storage-1", "Енергосховище «Буфер»", EnergyType.STORAGE, ObjectStatus.WORKING,
            "Літій-залізо-фосфатна батарея: накопичує надлишок і покриває дефіцит.",
            50.4600, 30.5400, 150.0, 62, at(21, 13, 4)
        ),
        EnergyObject(
            "storage-2", "Енергосховище «Резерв»", EnergyType.STORAGE, ObjectStatus.STANDBY,
            "Додаткова батарея для пікових навантажень увечері.",
            50.4400, 30.5700, 60.0, 15, at(20, 20, 0)
        ),
        EnergyObject(
            "substation", "Підстанція «Центр»", EnergyType.SUBSTATION, ObjectStatus.WORKING,
            "Вузол мікрогріду: розподіляє потужність і з'єднує систему із зовнішньою мережею.",
            50.4500, 30.5200, 400.0, 58, at(21, 13, 6)
        ),
        EnergyObject(
            "substation-s", "Підстанція «Південна»", EnergyType.SUBSTATION, ObjectStatus.WORKING,
            "Допоміжна підстанція для промислової зони.",
            50.3900, 30.5100, 250.0, 47, at(21, 12, 55)
        ),
        EnergyObject(
            "village", "Селище «Зелений Гай»", EnergyType.CONSUMER, ObjectStatus.WORKING,
            "Житлова забудова: вечірній пік споживання.",
            50.4900, 30.5800, 70.0, 66, at(21, 13, 0)
        ),
        EnergyObject(
            "workshop", "Виробничий цех «Агро-Маш»", EnergyType.CONSUMER, ObjectStatus.WORKING,
            "Промислове навантаження з денним піком споживання.",
            50.4100, 30.5400, 90.0, 83, at(21, 12, 57)
        ),
        EnergyObject(
            "hospital", "Лікарня «Здоров'я»", EnergyType.CONSUMER, ObjectStatus.WORKING,
            "Критичне навантаження: живиться першочергово, є резервний ввід.",
            50.4950, 30.5300, 110.0, 72, at(21, 13, 3)
        ),
        EnergyObject(
            "school", "Ліцей №5", EnergyType.CONSUMER, ObjectStatus.MAINTENANCE,
            "Заміна лічильників; навантаження тимчасово знято.",
            50.4650, 30.5000, 45.0, 0, at(18, 11, 15)
        ),
        EnergyObject(
            "farm", "Ферма «Агро-2»", EnergyType.CONSUMER, ObjectStatus.STANDBY,
            "Сезонне навантаження, зараз вимкнене.",
            50.4150, 30.6100, 55.0, 0, at(17, 16, 40)
        )
    )
}