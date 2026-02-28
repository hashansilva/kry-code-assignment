import { Question } from '../types';

export const QUESTIONS: Question[] = [
  {
    id: 'symptom-intensity',
    prompt: 'How would you describe your main symptom?',
    options: [
      { label: 'Mild discomfort', score: 1 },
      { label: 'Moderate pain', score: 2 },
      { label: 'Severe pain', score: 3 }
    ]
  },
  {
    id: 'symptom-duration',
    prompt: 'How long have you had this symptom?',
    options: [
      { label: 'Less than 24 hours', score: 1 },
      { label: '1-3 days', score: 2 },
      { label: 'More than 3 days', score: 3 }
    ]
  },
  {
    id: 'daily-impact',
    prompt: 'Does the symptom affect your ability to carry out daily activities?',
    options: [
      { label: 'Not at all', score: 1 },
      { label: 'Somewhat', score: 2 },
      { label: 'Significantly', score: 3 }
    ]
  },
  {
    id: 'history',
    prompt: 'Have you experienced this symptom before?',
    options: [
      { label: 'Yes, and it resolved on its own', score: 1 },
      { label: 'Yes, and it needed treatment', score: 2 },
      { label: 'No, this is new', score: 3 }
    ]
  },
  {
    id: 'red-flags',
    prompt: 'Do you have any of the following: fever above 38C, difficulty breathing, or chest pain?',
    options: [
      { label: 'None of these', score: 1 },
      { label: 'One of these', score: 2 },
      { label: 'Two or more', score: 3 }
    ]
  }
];
