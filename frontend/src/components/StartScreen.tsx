import { Button, Chip, Stack, Typography } from '@mui/material';

interface StartScreenProps {
  waitMessage: string;
  upcomingBookingText: string | null;
  onStart: () => void;
}

function StartScreen({ waitMessage, upcomingBookingText, onStart }: StartScreenProps) {
  return (
    <Stack spacing={3} className="screen-enter">
      <Typography variant="h3" className="hero-title">
        Care guidance, fast.
      </Typography>
      <Typography variant="h6" color="text.secondary">
        {waitMessage}
      </Typography>
      {upcomingBookingText ? (
        <Chip color="success" label={`Upcoming meeting: ${upcomingBookingText}`} />
      ) : null}
      <Button variant="contained" size="large" onClick={onStart}>
        Book Meeting
      </Button>
    </Stack>
  );
}

export default StartScreen;
