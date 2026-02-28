import { Alert, Box, Button, Card, CardContent, Pagination, Stack, Typography } from '@mui/material';
import { Recommendation } from '../types';

interface RecommendationScreenProps {
  recommendation: Recommendation;
  slots: string[];
  selectedSlot: string | null;
  page: number;
  totalPages: number;
  totalSlots: number;
  isLoadingSlots: boolean;
  bookingError: string | null;
  isBooking: boolean;
  onSelectSlot: (slot: string) => void;
  onPageChange: (page: number) => void;
  onConfirm: () => void;
  onRestart: () => void;
}

const recommendationDescriptions: Record<Recommendation, string> = {
  Chat: 'Your symptoms appear suitable for chat-first support. A clinician can guide next steps.',
  Nurse: 'A nurse review is recommended for closer assessment and treatment guidance.',
  Doctor: 'A doctor consultation is recommended due to higher clinical urgency.'
};

function formatSlot(slot: string): string {
  return new Date(slot).toLocaleString([], {
    weekday: 'short',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
}

function RecommendationScreen({
  recommendation,
  slots,
  selectedSlot,
  page,
  totalPages,
  totalSlots,
  isLoadingSlots,
  bookingError,
  isBooking,
  onSelectSlot,
  onPageChange,
  onConfirm,
  onRestart
}: RecommendationScreenProps) {
  return (
    <Stack spacing={3} className="screen-enter">
      <Card>
        <CardContent>
          <Typography variant="h5" gutterBottom>
            Recommendation: {recommendation}
          </Typography>
          <Typography color="text.secondary">{recommendationDescriptions[recommendation]}</Typography>
        </CardContent>
      </Card>

      <Stack spacing={1}>
        <Typography variant="h6">Available Slots</Typography>
        <Typography variant="body2" color="text.secondary">
          {totalSlots} slots found
        </Typography>
        <Box className="slots-grid">
          {slots.length === 0 ? (
            <Alert severity="warning">No slots currently available. Please restart and try again.</Alert>
          ) : (
            slots.map((slot) => (
              <Button
                key={slot}
                className="slot-button"
                variant={selectedSlot === slot ? 'contained' : 'outlined'}
                onClick={() => onSelectSlot(slot)}
                fullWidth
                disabled={isLoadingSlots}
              >
                {formatSlot(slot)}
              </Button>
            ))
          )}
        </Box>
        {totalPages > 1 ? (
          <Stack direction="row" justifyContent="center" pt={1}>
            <Pagination
              count={totalPages}
              page={page + 1}
              onChange={(_, value) => onPageChange(value - 1)}
              color="primary"
              shape="rounded"
              disabled={isLoadingSlots}
            />
          </Stack>
        ) : null}
      </Stack>

      {bookingError ? <Alert severity="error">{bookingError}</Alert> : null}

      <Stack direction="row" justifyContent="space-between">
        <Button onClick={onRestart}>Start Over</Button>
        <Button
          variant="contained"
          disabled={!selectedSlot || slots.length === 0 || isBooking || isLoadingSlots}
          onClick={onConfirm}
        >
          {isBooking ? 'Confirming...' : 'Confirm Booking'}
        </Button>
      </Stack>
    </Stack>
  );
}

export default RecommendationScreen;
