import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { CloudService } from '../../services/cloud.service';

interface GcpFile {
  filename: string;
  path: string;
  size: number;
  updated: any;
}

interface ProcessedSheet {
  sheetName: string;
  referenceId: string;
  rowCount: string;
}

@Component({
  selector: 'app-excel-processor',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatChipsModule,
    MatProgressBarModule,
    MatSnackBarModule
  ],
  templateUrl: './excel-processor.component.html',
  styleUrl: './excel-processor.component.scss'
})
export class ExcelProcessorComponent implements OnInit {
  fileDisplayedColumns: string[] = ['filename', 'path', 'size', 'actions'];
  sheetDisplayedColumns: string[] = ['sheetName', 'referenceId', 'rowCount', 'actions'];
  
  loadingFiles = false;
  processingFile = false;
  gcpFiles = new MatTableDataSource<any>([]);
  processedSheets = new MatTableDataSource<ProcessedSheet>([]);
  
  selectedFilename: string | null = null;
  
  constructor(
    private cloudService: CloudService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadFiles();
  }

  loadFiles(): void {
    this.loadingFiles = true;
    this.cloudService.listGcpFiles().subscribe({
      next: (response) => {
        this.loadingFiles = false;
        if (response.success && response.data) {
          // Filter to show only Excel files
          const excelFiles = response.data.filter((file: any) => 
            file.filename.endsWith('.xlsx') || 
            file.filename.endsWith('.xls') ||
            file.filename.endsWith('.xlsm')
          );
          this.gcpFiles.data = excelFiles;
        } else {
          this.gcpFiles.data = [];
        }
      },
      error: (error) => {
        this.loadingFiles = false;
        this.snackBar.open(`Failed to load files: ${error.message}`, 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
        this.gcpFiles.data = [];
      }
    });
  }

  processExcelFile(filename: string): void {
    this.processingFile = true;
    this.selectedFilename = filename;
    
    this.cloudService.parseExcelFromGcp(filename).subscribe({
      next: (response) => {
        this.processingFile = false;
        if (response.success && response.data) {
          this.selectedFilename = null;
          this.processedSheets.data = response.data.cachedSheets || [];
          this.snackBar.open(`Successfully parsed ${response.data.totalSheets} sheets from ${filename}`, 'Close', {
            duration: 5000,
            panelClass: ['success-snackbar']
          });
        } else {
          this.snackBar.open('Failed to parse Excel file', 'Close', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        }
      },
      error: (error) => {
        this.processingFile = false;
        this.selectedFilename = null;
        this.snackBar.open(`Failed to process file: ${error.message}`, 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  viewSheetDetails(referenceId: string): void {
    this.snackBar.open(`Viewing sheet details for: ${referenceId}`, 'Close', {
      duration: 3000
    });
    // TODO: Navigate to sheet details or open dialog
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  formatDate(date: any): string {
    if (!date) return 'N/A';
    const d = new Date(date);
    return d.toLocaleString();
  }

  isProcessing(file: any): boolean {
    return this.processingFile && this.selectedFilename === file.filename;
  }
}

