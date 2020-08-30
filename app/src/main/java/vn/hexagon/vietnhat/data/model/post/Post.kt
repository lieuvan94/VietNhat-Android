package vn.hexagon.vietnhat.data.model.post

import com.google.gson.annotations.SerializedName
import vn.hexagon.vietnhat.data.model.comment.Comment
import vn.hexagon.vietnhat.data.model.detail.Images

data class Post(
    @SerializedName("active")
    val active: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("address_receive")
    val addressReceive: String,
    @SerializedName("address_send")
    val addressSend: String,
    @SerializedName("car_name")
    val carName: String,
    @SerializedName("car_type")
    val carType: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("city_id")
    val cityId: String,
    @SerializedName("company_name")
    val companyName: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("img")
    val img: String,
    @SerializedName("is_favourite")
    var isFavourite: Int,
    @SerializedName("is_top")
    val isTop: String,
    @SerializedName("job_category_id")
    val jobCategoryId: String,
    @SerializedName("job_type_id")
    val jobTypeId: String,
    @SerializedName("max_price")
    val maxPrice: String,
    @SerializedName("min_price")
    val minPrice: String,
    @SerializedName("name_user_post")
    val nameUserPost: String,
    @SerializedName("note")
    val note: String,
    @SerializedName("number")
    val number: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("price")
    val price: String,
    @SerializedName("product_type")
    val productType: String,
    @SerializedName("road_type")
    val roadType: String,
    @SerializedName("salary")
    val salary: String,
    @SerializedName("service_id")
    val serviceId: String,
    @SerializedName("subject")
    val subject: String,
    @SerializedName("subject_id")
    val subjectId: String,
    @SerializedName("time_up_top")
    val timeUpTop: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("unit")
    val unit: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("weight")
    val weight: String,
    @SerializedName("list_image")
    val imagesList: List<Images>,
    @SerializedName("user_name")
    val userName: String,
    @SerializedName("user_avatar")
    val userAvatar: String,
    @SerializedName("subject_name")
    val subjectName: String,
    @SerializedName("translate_type")
    val translateType: String,
    @SerializedName("salary_type")
    val salaryType: String,
    @SerializedName("job_category")
    val jobCategory: String,
    @SerializedName("list_comment")
    val comments: ArrayList<Comment>,
    @SerializedName("list_favourite")
    val likes: ArrayList<Comment>
)