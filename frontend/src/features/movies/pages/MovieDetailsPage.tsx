import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { MovieDetails, getMovieDetails } from "../../../api/moviesApi";
import { getMyRating, upsertRating } from "../../../api/ratingsApi";
import { Review, createReview, deleteReview, listReviews, updateReview } from "../../../api/reviewsApi";
import { useAuth } from "../../auth/context/AuthContext";
import { StarRatingInput } from "../../ratings/components/StarRatingInput";
import { ReviewEditor } from "../../reviews/components/ReviewEditor";
import { ReviewList } from "../../reviews/components/ReviewList";

export function MovieDetailsPage() {
  const { id } = useParams<{ id: string }>();
  const { isAuthenticated, user } = useAuth();
  const [movie, setMovie] = useState<MovieDetails | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");
  const [selectedScore, setSelectedScore] = useState<number | null>(null);
  const [isSavingRating, setIsSavingRating] = useState(false);
  const [ratingMessage, setRatingMessage] = useState("");
  const [reviews, setReviews] = useState<Review[]>([]);
  const [reviewMessage, setReviewMessage] = useState("");
  const [editingReview, setEditingReview] = useState<Review | null>(null);
  const currentUserId = user?.id ?? null;

  useEffect(() => {
    if (!id) {
      setError("Movie id is missing.");
      setIsLoading(false);
      return;
    }

    const movieId = id;

    let isMounted = true;
    async function loadMovie() {
      setIsLoading(true);
      setError("");
      try {
        const data = await getMovieDetails(movieId);
        const myRating = isAuthenticated ? await getMyRating(movieId) : null;
        const reviewItems = await listReviews(movieId);
        if (isMounted) {
          setMovie(data);
          setSelectedScore(myRating?.score ?? null);
          setReviews(reviewItems);
        }
      } catch (err) {
        if (isMounted) {
          setMovie(null);
          setError(err instanceof Error ? err.message : "Unable to load movie details");
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    loadMovie();
    return () => {
      isMounted = false;
    };
  }, [id, isAuthenticated]);

  async function handleSaveRating() {
    if (!isAuthenticated) {
      setRatingMessage("Please sign in to save a rating.");
      return;
    }

    if (!movie || selectedScore == null) {
      return;
    }
    setRatingMessage("");
    setIsSavingRating(true);
    try {
      const saved = await upsertRating(movie.id, selectedScore);
      setSelectedScore(saved.score);
      setRatingMessage(`Saved your rating: ${saved.score}/5`);
    } catch (err) {
      setRatingMessage(err instanceof Error ? err.message : "Unable to save rating");
    } finally {
      setIsSavingRating(false);
    }
  }

  async function handleCreateReview(text: string, containsSpoilers: boolean) {
    if (!isAuthenticated) {
      setReviewMessage("Please sign in to post a review.");
      return;
    }

    if (!movie) {
      return;
    }
    const created = await createReview(movie.id, text, containsSpoilers);
    setReviews((prev) => [created, ...prev]);
    setReviewMessage("Review posted.");
  }

  async function handleUpdateReview(text: string, containsSpoilers: boolean) {
    if (!isAuthenticated) {
      setReviewMessage("Please sign in to update a review.");
      return;
    }

    if (!editingReview) {
      return;
    }
    const updated = await updateReview(editingReview.id, text, containsSpoilers);
    setReviews((prev) => prev.map((item) => (item.id === updated.id ? updated : item)));
    setEditingReview(null);
    setReviewMessage("Review updated.");
  }

  async function handleDeleteReview(reviewId: number) {
    if (!isAuthenticated) {
      setReviewMessage("Please sign in to delete a review.");
      return;
    }

    await deleteReview(reviewId);
    setReviews((prev) => prev.filter((item) => item.id !== reviewId));
    setReviewMessage("Review deleted.");
  }

  if (isLoading) {
    return <section><h2>Movie Details</h2><p>Loading movie details...</p></section>;
  }

  if (error) {
    return <section><h2>Movie Details</h2><p className="error-text">{error}</p></section>;
  }

  if (!movie) {
    return <section><h2>Movie Details</h2><p>Movie not found.</p></section>;
  }

  return (
    <section>
      <h2>{movie.title}</h2>
      <p><strong>Release year:</strong> {movie.releaseYear}</p>
      <p>{movie.synopsis}</p>
      <p><strong>Movie ID:</strong> {movie.id}</p>

      <section className="rating-panel">
        <h3>Your Rating</h3>
        <StarRatingInput value={selectedScore} onChange={setSelectedScore} />
        <button
          type="button"
          onClick={handleSaveRating}
          disabled={!isAuthenticated || selectedScore == null || isSavingRating}
        >
          {isSavingRating ? "Saving..." : "Save rating"}
        </button>
        {!isAuthenticated && <p>Sign in to save a personal rating.</p>}
        {ratingMessage && <p>{ratingMessage}</p>}
      </section>

      <section className="review-panel">
        <h3>Reviews</h3>
        {isAuthenticated && editingReview ? (
          <ReviewEditor
            initialText={editingReview.reviewText}
            initialContainsSpoilers={editingReview.containsSpoilers}
            onSave={handleUpdateReview}
            onCancel={() => setEditingReview(null)}
            submitLabel="Update review"
          />
        ) : isAuthenticated ? (
          <ReviewEditor onSave={handleCreateReview} submitLabel="Post review" />
        ) : (
          <p>Sign in to write and manage your own reviews.</p>
        )}
        {reviewMessage && <p>{reviewMessage}</p>}
        <ReviewList
          reviews={reviews}
          currentUserId={currentUserId}
          onEdit={setEditingReview}
          onDelete={handleDeleteReview}
        />
      </section>
    </section>
  );
}
