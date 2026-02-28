export type Recommendation = 'Chat' | 'Nurse' | 'Doctor';

export interface AssessmentResponse {
  recommendation: Recommendation;
  availableSlots: string[];
}

export interface BookingRequest {
  slot: string;
  recommendation: Recommendation;
}

export interface BookingResponse {
  confirmationId: string;
  slot: string;
  recommendation: Recommendation;
}

export interface QuestionOption {
  label: string;
  score: number;
}

export interface Question {
  id: string;
  prompt: string;
  options: QuestionOption[];
}
