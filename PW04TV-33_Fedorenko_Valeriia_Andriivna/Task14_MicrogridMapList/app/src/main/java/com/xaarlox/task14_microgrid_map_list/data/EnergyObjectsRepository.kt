package com.xaarlox.task14_microgrid_map_list.data

import com.xaarlox.task14_microgrid_map_list.domain.EnergyObject
import com.xaarlox.task14_microgrid_map_list.domain.EnergyType

/**
 * Data source: a simulation of the objects of a local microgrid with renewable sources
 * (the Kyiv area). In a real app this would be a database or a network request;
 * the UI works only with a collection, so the data is easy to replace
 */
object EnergyObjectsRepository {

    val objects: List<EnergyObject> = listOf(
        EnergyObject(
            id = "solar-north",
            name = "СЕС «Оболонь»",
            type = EnergyType.SOLAR,
            description = "Наземна сонячна станція з трекерами; основне джерело генерації вдень.",
            latitude = 50.5200,
            longitude = 30.4900,
            power = 120.0
        ), EnergyObject(
            id = "solar-roof",
            name = "Дахова СЕС «Теплиця»",
            type = EnergyType.SOLAR,
            description = "Сонячні панелі на даху тепличного комплексу.",
            latitude = 50.4300,
            longitude = 30.6000,
            power = 60.0
        ), EnergyObject(
            id = "wind-1",
            name = "Вітрогенератор «Вітер-1»",
            type = EnergyType.WIND,
            description = "Горизонтально-осьова турбіна, працює вночі та за хмарної погоди.",
            latitude = 50.4000,
            longitude = 30.4200,
            power = 80.0
        ), EnergyObject(
            id = "wind-2",
            name = "Вітрогенератор «Вітер-2»",
            type = EnergyType.WIND,
            description = "Друга турбіна на західній ділянці з найстабільнішим вітром.",
            latitude = 50.4700,
            longitude = 30.3600,
            power = 80.0
        ), EnergyObject(
            id = "storage-1",
            name = "Енергосховище «Буфер»",
            type = EnergyType.STORAGE,
            description = "Літій-залізо-фосфатна батарея: накопичує надлишок і покриває дефіцит.",
            latitude = 50.4600,
            longitude = 30.5400,
            power = 150.0
        ), EnergyObject(
            id = "substation",
            name = "Підстанція «Центр»",
            type = EnergyType.SUBSTATION,
            description = "Вузол мікрогріду: розподіляє потужність і з'єднує систему із зовнішньою мережею.",
            latitude = 50.4500,
            longitude = 30.5200,
            power = 400.0
        ), EnergyObject(
            id = "village",
            name = "Селище «Зелений Гай»",
            type = EnergyType.CONSUMER,
            description = "Житлова забудова: вечірній пік споживання.",
            latitude = 50.4900,
            longitude = 30.5800,
            power = 70.0
        ), EnergyObject(
            id = "workshop",
            name = "Виробничий цех «Агро-Маш»",
            type = EnergyType.CONSUMER,
            description = "Промислове навантаження з денним піком споживання.",
            latitude = 50.4100,
            longitude = 30.5400,
            power = 90.0
        )
    )
}