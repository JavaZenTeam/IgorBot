#!/usr/bin/env pwsh
$ErrorActionPreference = "Stop"

Write-Host "Running IgorBot verification pipeline..." -ForegroundColor Cyan

Write-Host "[1/2] Running tests" -ForegroundColor Yellow
mvn -B -ntp test

Write-Host "[2/2] Running package build" -ForegroundColor Yellow
mvn -B -ntp clean package

Write-Host "Verification completed successfully." -ForegroundColor Green
