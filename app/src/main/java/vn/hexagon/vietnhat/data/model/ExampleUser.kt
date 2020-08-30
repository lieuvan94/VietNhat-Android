package vn.hexagon.vietnhat.data.model

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

/**
 * Created by NhamVD on 2019-07-29.
 */
@Entity(primaryKeys = ["login"])
data class ExampleUser(
    @field:SerializedName("login")
    val login: String,
    @field:SerializedName("avatar_url")
    val avatarUrl: String?,
    @field:SerializedName("name")
    val name: String?,
    @field:SerializedName("company")
    val company: String?,
    @field:SerializedName("repos_url")
    val reposUrl: String?,
    @field:SerializedName("blog")
    val blog: String?
)