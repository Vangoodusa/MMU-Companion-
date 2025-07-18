package com.aeci.mmucompanion.domain.usecase

import com.aeci.mmucompanion.data.export.PdfExportEngine
import com.aeci.mmucompanion.domain.repository.FormRepository
import javax.inject.Inject

class GenerateBlastReportUseCase @Inject constructor(
    private val formRepository: FormRepository,
    private val pdfExportEngine: PdfExportEngine,
    private val exportFormUseCase: ExportFormUseCase
) {
    suspend fun execute(
        blastHoleLogId: String,
        dailyLogId: String,
        qualityReportId: String,
        pretaskId: String,
        outputPath: String
    ): Result<String> {
        return try {
            // 1. Export individual forms to PDF
            val blastHolePdf = exportFormUseCase.exportToPdf(blastHoleLogId).getOrThrow()
            val dailyLogPdf = exportFormUseCase.exportToPdf(dailyLogId).getOrThrow()
            val qualityReportPdf = exportFormUseCase.exportToPdf(qualityReportId).getOrThrow()
            val pretaskPdf = exportFormUseCase.exportToPdf(pretaskId).getOrThrow()

            // 2. Gather data for summary
            val summaryData = mutableMapOf<String, String>()
            formRepository.getFormById(blastHoleLogId)?.let { summaryData["Blast Hole Log"] = "ID: ${it.id}" }
            formRepository.getFormById(dailyLogId)?.let { summaryData["Production Daily Log"] = "Date: ${it.createdAt}" }
            formRepository.getFormById(qualityReportId)?.let { summaryData["Quality Report"] = "Status: ${it.status}" }
            formRepository.getFormById(pretaskId)?.let { summaryData["Pre-Task Assessment"] = "Completed: Yes" }
            
            // 3. Create summary PDF
            val summaryPdfPath = outputPath.replace(".pdf", "_summary.pdf")
            pdfExportEngine.createBlastReportSummary(summaryData, summaryPdfPath)

            // 4. Merge all PDFs
            val allPdfs = listOf(summaryPdfPath, blastHolePdf, dailyLogPdf, qualityReportPdf, pretaskPdf)
            pdfExportEngine.mergePdfs(allPdfs, outputPath)

            Result.success(outputPath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 