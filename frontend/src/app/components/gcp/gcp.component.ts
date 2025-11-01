import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { CloudService } from '../../services/cloud.service';

@Component({
  selector: 'app-gcp',
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
  templateUrl: './gcp.component.html',
  styleUrl: './gcp.component.scss'
})
export class GcpComponent implements OnInit {
  fileDisplayedColumns: string[] = ['filename', 'path', 'size', 'updated', 'actions'];
  
  isUploading = false;
  uploadProgress = 0;
  loadingFiles = false;
  
  gcpFiles = new MatTableDataSource<any>([]);
  
  constructor(
    private cloudService: CloudService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadFiles();
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.uploadFile(input.files[0]);
    }
  }

  uploadFile(file: File): void {
    this.isUploading = true;
    this.uploadProgress = 0;

    this.cloudService.uploadToGcp(file).subscribe({
      next: (response) => {
        this.uploadProgress = 100;
        this.isUploading = false;
        this.snackBar.open(`File "${file.name}" uploaded successfully to GCP`, 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
        console.log('Upload successful:', response);
        this.loadFiles(); // Reload the file list
      },
      error: (error) => {
        this.isUploading = false;
        this.uploadProgress = 0;
        this.snackBar.open(`Failed to upload file: ${error.message}`, 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
        console.error('Upload error:', error);
      }
    });
  }

  loadFiles(): void {
    this.loadingFiles = true;
    this.cloudService.listGcpFiles().subscribe({
      next: (response) => {
        this.loadingFiles = false;
        if (response.success && response.data) {
          this.gcpFiles.data = response.data;
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

  downloadFile(filename: string): void {
    this.cloudService.downloadFromGcp(filename).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
        this.snackBar.open(`File "${filename}" downloaded successfully`, 'Close', {
          duration: 3000
        });
      },
      error: (error) => {
        this.snackBar.open(`Failed to download file: ${error.message}`, 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  deleteFile(filename: string): void {
    if (confirm(`Are you sure you want to delete "${filename}"?`)) {
      this.cloudService.deleteGcpFile(filename).subscribe({
        next: () => {
          this.snackBar.open(`File "${filename}" deleted successfully`, 'Close', {
            duration: 3000
          });
          this.loadFiles(); // Reload the list
        },
        error: (error) => {
          this.snackBar.open(`Failed to delete file: ${error.message}`, 'Close', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        }
      });
    }
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
}