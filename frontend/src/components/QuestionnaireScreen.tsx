import { Alert, Box, Button, Card, CardContent, Chip, LinearProgress, Radio, Stack, Typography } from '@mui/material';
import { Question } from '../types';

interface QuestionnaireScreenProps {
  question: Question;
  currentIndex: number;
  total: number;
  selectedScore: number | null;
  isSubmitting: boolean;
  error: string | null;
  onSelect: (score: number) => void;
  onBack: () => void;
  onJumpTo: (index: number) => void;
  onNext: () => void;
  onCancel: () => void;
}

function QuestionnaireScreen({
  question,
  currentIndex,
  total,
  selectedScore,
  isSubmitting,
  error,
  onSelect,
  onBack,
  onJumpTo,
  onNext,
  onCancel
}: QuestionnaireScreenProps) {
  const progress = ((currentIndex + 1) / total) * 100;

  return (
    <Stack spacing={3} className="screen-enter">
      <Stack direction="row" justifyContent="space-between" alignItems="center" flexWrap="wrap" gap={1}>
        <Chip label={`Question ${currentIndex + 1} of ${total}`} color="primary" />
        <Button variant="text" color="error" onClick={onCancel}>
          Cancel
        </Button>
      </Stack>

      <LinearProgress variant="determinate" value={progress} />
      <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
        {Array.from({ length: total }).map((_, idx) => (
          <Chip
            key={`step-${idx + 1}`}
            label={idx + 1}
            color={idx === currentIndex ? 'primary' : 'default'}
            variant={idx <= currentIndex ? 'filled' : 'outlined'}
            clickable={idx <= currentIndex}
            onClick={() => idx <= currentIndex && onJumpTo(idx)}
            size="small"
          />
        ))}
      </Stack>

      <Card>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            {question.prompt}
          </Typography>
          <Stack spacing={1.5} mt={2}>
            {question.options.map((option) => (
              <Box
                key={`${question.id}-${option.label}`}
                className={`option-row ${selectedScore === option.score ? 'selected' : ''}`}
                onClick={() => onSelect(option.score)}
                role="button"
                tabIndex={0}
                onKeyDown={(event) => {
                  if (event.key === 'Enter' || event.key === ' ') {
                    event.preventDefault();
                    onSelect(option.score);
                  }
                }}
                aria-label={`${option.label}, score ${option.score}`}
              >
                <Stack direction="row" alignItems="center" justifyContent="space-between">
                  <Typography>{option.label}</Typography>
                  <Stack direction="row" spacing={1} alignItems="center">
                    <Typography variant="body2" color="text.secondary">
                      Score {option.score}
                    </Typography>
                    <Radio checked={selectedScore === option.score} />
                  </Stack>
                </Stack>
              </Box>
            ))}
          </Stack>
        </CardContent>
      </Card>

      {error ? <Alert severity="error">{error}</Alert> : null}

      <Stack direction="row" justifyContent="space-between">
        <Button disabled={currentIndex === 0 || isSubmitting} onClick={onBack}>
          Back
        </Button>
        <Button
          variant="contained"
          disabled={selectedScore === null || isSubmitting}
          onClick={onNext}
        >
          {currentIndex === total - 1 ? (isSubmitting ? 'Submitting...' : 'Submit') : 'Next'}
        </Button>
      </Stack>
    </Stack>
  );
}

export default QuestionnaireScreen;
