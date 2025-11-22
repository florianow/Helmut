package com.helmut.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class TemplateWithTasks(
    @Embedded val template: Template,
    @Relation(
        parentColumn = "id",
        entityColumn = "templateId"
    )
    val tasks: List<TemplateTask>
)
