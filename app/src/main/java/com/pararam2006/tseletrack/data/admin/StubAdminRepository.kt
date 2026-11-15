package com.pararam2006.tseletrack.data.admin

import com.pararam2006.tseletrack.domain.role.AdminDashboard
import com.pararam2006.tseletrack.domain.role.AdminUser
import com.pararam2006.tseletrack.domain.role.RoleProfile
import com.pararam2006.tseletrack.domain.role.StageMetric
import com.pararam2006.tseletrack.domain.role.SystemEvent

/** In-memory administration data source. Replace with API calls when endpoints are available. */
class StubAdminRepository : AdminRepository {
    override suspend fun loadDashboard(): AdminDashboard = AdminDashboard(
        userCount = 38,
        studentCount = 24,
        contractsWaiting = 3,
        employedPercent = 64,
        stages = listOf(
            StageMetric("Профориентация", 98),
            StageMetric("Выбор вуза", 86),
            StageMetric("Договор", 64),
            StageMetric("Обучение", 50),
            StageMetric("Трудоустройство", 38),
        ),
        users = listOf(
            AdminUser("anna", "Анна Смирнова", "anna@mail.ru", "А", "Студент"),
            AdminUser("tulgu", "ТулГУ (деканат)", "uni@tulsu.ru", "Т", "Вуз"),
            AdminUser("factory", "ООО «Завод»", "hr@zavod.ru", "О", "Работодатель"),
            AdminUser("admin", "Админ", "admin@th.ru", "А", "Админ"),
            AdminUser("b", "Студент Б.", "b@mail.ru", "С", "Студент"),
            AdminUser("v", "Студент В.", "v@mail.ru", "С", "Студент"),
        ),
        events = listOf(
            SystemEvent("Студент А. подписал договор", "14.11"),
            SystemEvent("Вуз подтвердил договор", "14.11"),
            SystemEvent("Новый пользователь", "13.11"),
            SystemEvent("Экспорт данных", "12.11"),
        ),
        profile = RoleProfile("Администратор", "А", "Админ"),
    )

    override suspend fun exportData() = Unit
    override suspend fun openRoles() = Unit
    override suspend fun createBackup() = Unit
    override suspend fun resetData() = Unit
    override suspend fun addUser() = Unit
    override suspend fun logout() = Unit
}
