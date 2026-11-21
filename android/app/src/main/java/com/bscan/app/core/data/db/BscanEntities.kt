package com.bscan.app.core.data.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "created_at_epoch_ms")
    val createdAtEpochMs: Long,
    @ColumnInfo(name = "updated_at_epoch_ms")
    val updatedAtEpochMs: Long,
)

@Entity(
    tableName = "pages",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["project_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["project_id", "page_order"]),
    ],
)
data class PageEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "project_id")
    val projectId: String,
    @ColumnInfo(name = "page_order")
    val pageOrder: Int,
    @ColumnInfo(name = "display_name")
    val displayName: String,
    @ColumnInfo(name = "original_file_path")
    val originalFilePath: String,
    @ColumnInfo(name = "thumbnail_file_path")
    val thumbnailFilePath: String?,
    @ColumnInfo(name = "width_px")
    val widthPx: Int,
    @ColumnInfo(name = "height_px")
    val heightPx: Int,
    @ColumnInfo(name = "file_size_bytes")
    val fileSizeBytes: Long,
    @ColumnInfo(name = "mime_type")
    val mimeType: String,
    @ColumnInfo(name = "created_at_epoch_ms")
    val createdAtEpochMs: Long,
)

data class ProjectWithPages(
    @Embedded
    val project: ProjectEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "project_id",
    )
    val pages: List<PageEntity>,
)
