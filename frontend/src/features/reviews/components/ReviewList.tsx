import { Review } from "../../../api/reviewsApi";

type ReviewListProps = {
  reviews: Review[];
  currentUserId: number | null;
  onEdit: (review: Review) => void;
  onDelete: (reviewId: number) => Promise<void>;
};

export function ReviewList({ reviews, currentUserId, onEdit, onDelete }: ReviewListProps) {
  if (reviews.length === 0) {
    return <p>No reviews yet. Be the first to write one.</p>;
  }

  return (
    <ul className="review-list">
      {reviews.map((review) => {
        const isMine = currentUserId != null && review.userId === currentUserId;
        return (
          <li key={review.id} className="review-item">
            <p>{review.reviewText}</p>
            {review.containsSpoilers && <p className="spoiler-tag">Contains spoilers</p>}
            <small>Review #{review.id} by user {review.userId}</small>
            {isMine && (
              <div className="review-actions">
                <button type="button" onClick={() => onEdit(review)}>Edit</button>
                <button type="button" onClick={() => void onDelete(review.id)}>Delete</button>
              </div>
            )}
          </li>
        );
      })}
    </ul>
  );
}
