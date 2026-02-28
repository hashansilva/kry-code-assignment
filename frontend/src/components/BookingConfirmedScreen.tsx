import { Button, Card, CardContent, Stack, Typography } from '@mui/material';
import { BookingResponse } from '../types';

interface BookingConfirmedScreenProps {
  booking: BookingResponse;
  onReturnHome: () => void;
}

function BookingConfirmedScreen({ booking, onReturnHome }: BookingConfirmedScreenProps) {
  const slotLabel = new Date(booking.slot).toLocaleString([], {
    weekday: 'short',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });

  return (
    <Stack spacing={3} className="screen-enter">
      <Card>
        <CardContent>
          <Typography variant="h5" gutterBottom>
            Booking Confirmed
          </Typography>
          <Typography>Your appointment is scheduled for {slotLabel}.</Typography>
          <Typography color="text.secondary" mt={1}>
            Confirmation ID: {booking.confirmationId}
          </Typography>
          <Typography color="text.secondary">Care path: {booking.recommendation}</Typography>
        </CardContent>
      </Card>
      <Button variant="contained" onClick={onReturnHome}>
        Return to Home
      </Button>
    </Stack>
  );
}

export default BookingConfirmedScreen;
