import { useEffect, useState } from 'react';
import { Alert, CircularProgress, Container, CssBaseline, Stack, ThemeProvider, Typography, createTheme } from '@mui/material';
import StartScreen from './components/StartScreen';
import QuestionnaireScreen from './components/QuestionnaireScreen';
import RecommendationScreen from './components/RecommendationScreen';
import BookingConfirmedScreen from './components/BookingConfirmedScreen';
import { QUESTIONS } from './constants/questions';
import { fetchNextAvailability, submitAssessment, submitBooking } from './api/client';
import { AssessmentResponse, BookingResponse } from './types';

import './styles/app.scss';

type Stage = 'start' | 'questionnaire' | 'recommendation' | 'confirmed';

const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: '#0d6e6e'
    },
    secondary: {
      main: '#f47c48'
    },
    background: {
      default: '#f2f7f5'
    }
  },
  typography: {
    fontFamily: '"Poppins", "Segoe UI", sans-serif'
  },
  shape: {
    borderRadius: 14
  }
});

function formatWaitMessage(nextAvailableSlot: string | null): string {
  if (!nextAvailableSlot) {
    return 'No appointments available in the next 3 days';
  }

  const minutesUntil = Math.max(
    0,
    Math.ceil((new Date(nextAvailableSlot).getTime() - Date.now()) / (1000 * 60))
  );

  if (minutesUntil < 60) {
    return `See a doctor in ${minutesUntil} min${minutesUntil === 1 ? '' : 's'}`;
  }

  const hours = Math.floor(minutesUntil / 60);
  const minutes = minutesUntil % 60;

  if (minutes === 0) {
    return `See a doctor in ${hours} hr${hours === 1 ? '' : 's'}`;
  }

  return `See a doctor in ${hours} hr${hours === 1 ? '' : 's'} ${minutes} mins`;
}

function App() {
  const pageSize = 12;
  const [stage, setStage] = useState<Stage>('start');
  const [questionIndex, setQuestionIndex] = useState(0);
  const [answers, setAnswers] = useState<(number | null)[]>(Array(QUESTIONS.length).fill(null));
  const [assessmentResult, setAssessmentResult] = useState<AssessmentResponse | null>(null);
  const [slotsPage, setSlotsPage] = useState(0);
  const [selectedSlot, setSelectedSlot] = useState<string | null>(null);
  const [confirmedBooking, setConfirmedBooking] = useState<BookingResponse | null>(null);

  const [isSubmittingAssessment, setIsSubmittingAssessment] = useState(false);
  const [isSubmittingBooking, setIsSubmittingBooking] = useState(false);
  const [assessmentError, setAssessmentError] = useState<string | null>(null);
  const [bookingError, setBookingError] = useState<string | null>(null);
  const [waitMessage, setWaitMessage] = useState('Checking current availability...');

  useEffect(() => {
    if (stage !== 'start') {
      return undefined;
    }

    let isActive = true;

    const loadWaitTime = async () => {
      try {
        const response = await fetchNextAvailability();
        if (isActive) {
          setWaitMessage(formatWaitMessage(response.nextAvailableSlot));
        }
      } catch {
        if (isActive) {
          setWaitMessage('Current availability is temporarily unavailable');
        }
      }
    };

    void loadWaitTime();
    const intervalId = window.setInterval(loadWaitTime, 60_000);

    return () => {
      isActive = false;
      window.clearInterval(intervalId);
    };
  }, [stage]);

  const currentQuestion = QUESTIONS[questionIndex];
  const selectedScore = answers[questionIndex];
  const allSlots = assessmentResult?.availableSlots ?? [];
  const totalPages = allSlots.length === 0 ? 0 : Math.ceil(allSlots.length / pageSize);
  const pagedSlots = allSlots.slice(slotsPage * pageSize, slotsPage * pageSize + pageSize);

  const resetFlow = () => {
    setStage('start');
    setQuestionIndex(0);
    setAnswers(Array(QUESTIONS.length).fill(null));
    setAssessmentResult(null);
    setSlotsPage(0);
    setSelectedSlot(null);
    setAssessmentError(null);
    setBookingError(null);
    setIsSubmittingAssessment(false);
    setIsSubmittingBooking(false);
  };

  const startQuestionnaire = () => {
    setStage('questionnaire');
    setAssessmentError(null);
    setBookingError(null);
  };

  const handleSelectAnswer = (score: number) => {
    const nextAnswers = [...answers];
    nextAnswers[questionIndex] = score;
    setAnswers(nextAnswers);
  };

  const handleBack = () => {
    setQuestionIndex((prev) => Math.max(0, prev - 1));
  };

  const handleJumpToQuestion = (index: number) => {
    if (index < 0 || index > questionIndex) {
      return;
    }
    setQuestionIndex(index);
  };

  const submitAssessmentFlow = async () => {
    let totalScore = 0;
    for (const value of answers) {
      if (value !== null) {
        totalScore += value;
      }
    }
    setIsSubmittingAssessment(true);
    setAssessmentError(null);

    try {
      const response = await submitAssessment(totalScore);
      const initialPage = 0;
      const initialSlots = response.availableSlots.slice(initialPage * pageSize, initialPage * pageSize + pageSize);

      setAssessmentResult(response);
      setSlotsPage(initialPage);
      setSelectedSlot(initialSlots[0] ?? null);
      setStage('recommendation');
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Failed to submit assessment.';
      setAssessmentError(message);
    } finally {
      setIsSubmittingAssessment(false);
    }
  };

  const handleSlotsPageChange = (page: number) => {
    setSlotsPage(page);
    const nextSlots = allSlots.slice(page * pageSize, page * pageSize + pageSize);
    setSelectedSlot(nextSlots[0] ?? null);
  };

  const handleNext = async () => {
    if (answers[questionIndex] === null) {
      return;
    }

    if (questionIndex < QUESTIONS.length - 1) {
      setQuestionIndex((prev) => prev + 1);
      return;
    }

    await submitAssessmentFlow();
  };

  const handleConfirmBooking = async () => {
    if (!assessmentResult || !selectedSlot) {
      return;
    }

    setIsSubmittingBooking(true);
    setBookingError(null);

    try {
      const response = await submitBooking({
        slot: selectedSlot,
        recommendation: assessmentResult.recommendation
      });
      setConfirmedBooking(response);
      setStage('confirmed');
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Failed to confirm booking.';
      setBookingError(message);
    } finally {
      setIsSubmittingBooking(false);
    }
  };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <div className="app-shell">
        <Container maxWidth="md" className="content-wrap">
          <Stack spacing={2}>
            <Typography variant="overline" color="primary">
              Medical Triage and Booking
            </Typography>

            {stage === 'start' && (
              <StartScreen
                waitMessage={waitMessage}
                upcomingBookingText={confirmedBooking ? new Date(confirmedBooking.slot).toLocaleString() : null}
                onStart={startQuestionnaire}
              />
            )}

            {stage === 'questionnaire' && (
              <QuestionnaireScreen
                question={currentQuestion}
                currentIndex={questionIndex}
                total={QUESTIONS.length}
                selectedScore={selectedScore}
                isSubmitting={isSubmittingAssessment}
                error={assessmentError}
                onSelect={handleSelectAnswer}
                onBack={handleBack}
                onJumpTo={handleJumpToQuestion}
                onNext={handleNext}
                onCancel={resetFlow}
              />
            )}

            {stage === 'recommendation' && assessmentResult && (
              <RecommendationScreen
                recommendation={assessmentResult.recommendation}
                slots={pagedSlots}
                selectedSlot={selectedSlot}
                page={slotsPage}
                totalPages={totalPages}
                totalSlots={allSlots.length}
                isLoadingSlots={false}
                bookingError={bookingError}
                isBooking={isSubmittingBooking}
                onSelectSlot={setSelectedSlot}
                onPageChange={handleSlotsPageChange}
                onConfirm={handleConfirmBooking}
                onRestart={resetFlow}
              />
            )}

            {stage === 'confirmed' && confirmedBooking && (
              <BookingConfirmedScreen
                booking={confirmedBooking}
                onReturnHome={resetFlow}
              />
            )}

            {(isSubmittingAssessment || isSubmittingBooking) && (
              <Stack direction="row" spacing={1} alignItems="center">
                <CircularProgress size={18} />
                <Typography color="text.secondary" variant="body2">
                  Processing request...
                </Typography>
              </Stack>
            )}

            {stage !== 'questionnaire' && assessmentError && (
              <Alert severity="error">{assessmentError}</Alert>
            )}
          </Stack>
        </Container>
      </div>
    </ThemeProvider>
  );
}

export default App;
