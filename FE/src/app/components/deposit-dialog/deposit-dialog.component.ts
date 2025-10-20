import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

export interface DepositDialogData {
  currentBalance: number;
}

export interface DepositDialogResult {
  amount: number;
}

@Component({
  selector: 'app-deposit-dialog',
  templateUrl: './deposit-dialog.component.html',
  styleUrls: ['./deposit-dialog.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule
  ]
})
export class DepositDialogComponent {
  depositForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<DepositDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DepositDialogData
  ) {
    this.depositForm = this.fb.group({
      amount: ['', [
        Validators.required,
        Validators.min(10.00), // Minimum $10.00 (1000 cents)
        Validators.pattern(/^\d+(\.\d{1,2})?$/) // Valid currency format
      ]]
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onConfirm(): void {
    if (this.depositForm.valid) {
      const amount = this.depositForm.get('amount')?.value;
      const result: DepositDialogResult = {
        amount: Math.round(amount * 100) // Convert to cents
      };
      this.dialogRef.close(result);
    }
  }
}