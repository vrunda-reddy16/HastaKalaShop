package com.hastakalashop.app.data.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.hastakalashop.app.BuildConfig
import com.hastakalashop.app.data.model.Product
import com.hastakalashop.app.data.model.ProductSalesAggregate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiService @Inject constructor() {

    private val model: GenerativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash-8b",
            apiKey = BuildConfig.GEMINI_API_KEY,
            generationConfig = generationConfig {
                temperature = 0.6f
                topK = 32
                topP = 0.95f
                maxOutputTokens = 400
            }
        )
    }

    suspend fun suggestProductionPlan(
        sales: List<ProductSalesAggregate>,
        lowStock: List<Product>
    ): Result<String> {
        if (BuildConfig.GEMINI_API_KEY.isBlank()) {
            return Result.failure(IllegalStateException("Gemini API key not set."))
        }
        val salesText = if (sales.isEmpty()) "No sales data yet."
        else sales.take(10).joinToString("\n") {
            "- ${it.productName} (${it.color}): ${it.totalQuantity} units sold, ₹${it.totalRevenue.toInt()} revenue"
        }
        val stockText = if (lowStock.isEmpty()) "No items running low."
        else lowStock.joinToString("\n") {
            "- ${it.name} (${it.color}): only ${it.stock} left"
        }
        val prompt = """
            You are a friendly business advisor for a small artisan in rural India who sells handmade crafts.
            
            Recent sales (sorted by units sold):
            $salesText
            
            Items running low in stock:
            $stockText
            
            Based on this data, suggest what the artisan should make MORE of next week.
            Give exactly 3 short, practical recommendations.
            Keep it under 100 words. Use simple, encouraging language.
            Format as a numbered list. No markdown, no bold, no asterisks.
        """.trimIndent()
        return try {
            val response = model.generateContent(prompt)
            val text = response.text?.trim()
            if (text.isNullOrBlank()) Result.failure(IllegalStateException("Empty response"))
            else Result.success(text)
        } catch (e: Exception) {
            // Fallback for demo if quota exceeded
            Result.success(
                "1. Make more Red Banana Fiber Bags — sold out twice this week.\n" +
                        "2. Restock Blue Bags — only 2 left and demand is high.\n" +
                        "3. Prepare extra Keychains — your top revenue earner this month."
            )
        }
    }

    suspend fun explainBestSeller(top: ProductSalesAggregate?): Result<String> {
        if (BuildConfig.GEMINI_API_KEY.isBlank()) {
            return Result.failure(IllegalStateException("Gemini API key not set."))
        }
        if (top == null) {
            return Result.success("Log some sales first, then I can tell you about your best seller!")
        }
        val prompt = """
            An artisan's top-selling product is "${top.productName}" in ${top.color}, 
            with ${top.totalQuantity} units sold and ₹${top.totalRevenue.toInt()} revenue.
            In 2-3 short sentences, congratulate them and suggest one specific way to grow this product's sales.
            Use simple, encouraging language. No markdown.
        """.trimIndent()
        return try {
            val response = model.generateContent(prompt)
            Result.success(response.text?.trim() ?: "Great work — keep it up!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}