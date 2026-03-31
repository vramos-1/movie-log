type StarRatingInputProps = {
  value: number | null;
  onChange: (score: number) => void;
};

export function StarRatingInput({ value, onChange }: StarRatingInputProps) {
  return (
    <div className="star-rating" role="radiogroup" aria-label="Movie rating">
      {[1, 2, 3, 4, 5].map((score) => {
        const isSelected = value !== null && score <= value;
        return (
          <button
            key={score}
            type="button"
            className={isSelected ? "star-btn selected" : "star-btn"}
            onClick={() => onChange(score)}
            aria-label={`Rate ${score} out of 5`}
          >
            {isSelected ? "★" : "☆"}
          </button>
        );
      })}
    </div>
  );
}
