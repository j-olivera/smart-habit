import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WeeklyReportService, WeeklyReportSummary, WeeklyReportResponse } from '../../services/habit/weekly-report.service';
import { marked } from 'marked';
import DOMPurify from 'dompurify';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reports.component.html',
  styleUrl: './reports.component.css'
})
export class ReportsComponent implements OnInit {
  private reportService = inject(WeeklyReportService);

  // State
  reportsList = signal<WeeklyReportSummary[]>([]);
  selectedReport = signal<WeeklyReportResponse | null>(null);
  isLoadingList = signal(false);
  isLoadingDetail = signal(false);
  isGenerating = signal(false);
  error = signal<string | null>(null);

  // Computed
  renderedMarkdown = computed(() => {
    const content = this.selectedReport()?.aiContent;
    if (!content) return '';
    const rawHtml = marked.parse(content) as string;
    return DOMPurify.sanitize(rawHtml);
  });

  ngOnInit(): void {
    this.loadReports();
  }

  loadReports(): void {
    this.isLoadingList.set(true);
    this.reportService.getReports().subscribe({
      next: (reports) => {
        this.reportsList.set(reports);
        if (reports.length > 0) {
          this.selectReport(reports[0].id);
        }
        this.isLoadingList.set(false);
      },
      error: (err) => {
        console.error('Error loading reports', err);
        this.error.set('Failed to load reports history.');
        this.isLoadingList.set(false);
      }
    });
  }

  selectReport(id: number): void {
    if (this.selectedReport()?.id === id) return;
    
    this.isLoadingDetail.set(true);
    this.reportService.getReportById(id).subscribe({
      next: (report) => {
        this.selectedReport.set(report);
        this.isLoadingDetail.set(false);
      },
      error: (err) => {
        console.error('Error loading report detail', err);
        this.error.set('Failed to load report details.');
        this.isLoadingDetail.set(false);
      }
    });
  }

  generateNewReport(): void {
    this.isGenerating.set(true);
    this.error.set(null);
    
    this.reportService.generateReport().subscribe({
      next: (newReport) => {
        this.selectedReport.set(newReport);
        // Refresh list to include new report
        this.loadReports();
        this.isGenerating.set(false);
      },
      error: (err) => {
        console.error('Error generating report', err);
        if (err.status === 400) {
          this.error.set('Insufficient data: You need at least 3 logged days this week.');
        } else {
          this.error.set('Failed to generate report. Please try again later.');
        }
        this.isGenerating.set(false);
      }
    });
  }

  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString(undefined, { 
      day: 'numeric', 
      month: 'short', 
      year: 'numeric' 
    });
  }
}
