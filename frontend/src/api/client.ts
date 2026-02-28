import { AssessmentResponse, BookingRequest, BookingResponse } from '../types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '/api';

async function parseResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Request failed: ${response.status}`);
  }
  return response.json() as Promise<T>;
}

export async function submitAssessment(score: number): Promise<AssessmentResponse> {
  const response = await fetch(`${API_BASE_URL}/assessment`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ score })
  });

  return parseResponse<AssessmentResponse>(response);
}

export async function submitBooking(payload: BookingRequest): Promise<BookingResponse> {
  const response = await fetch(`${API_BASE_URL}/booking`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(payload)
  });

  return parseResponse<BookingResponse>(response);
}
