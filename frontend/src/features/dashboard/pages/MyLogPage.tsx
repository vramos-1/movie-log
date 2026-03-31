import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { MovieLogEntry, getMyLog } from "../../../api/logApi";

const PAGE_SIZE = 2;

export function MyLogPage() {
  const [entries, setEntries] = useState<MovieLogEntry[]>([]);
  const [sort, setSort] = useState<"recent" | "title">("recent");
  const [page, setPage] = useState(1);
  const [total, setTotal] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;
    async function loadLog() {
      setIsLoading(true);
      setError("");
      try {
        const result = await getMyLog(sort, page, PAGE_SIZE);
        if (active) {
          setEntries(result.data);
          setTotal(result.meta?.total ?? 0);
        }
      } catch (err) {
        if (active) {
          setEntries([]);
          setError(err instanceof Error ? err.message : "Unable to load movie log");
        }
      } finally {
        if (active) {
          setIsLoading(false);
        }
      }
    }

    loadLog();
    return () => {
      active = false;
    };
  }, [page, sort]);

  const totalPages = Math.max(1, Math.ceil(total / PAGE_SIZE));

  return (
    <section>
      <div className="dashboard-header">
        <h2>My Movie Log</h2>
        <label>
          Sort by{" "}
          <select value={sort} onChange={(event) => {
            setSort(event.target.value as "recent" | "title");
            setPage(1);
          }}>
            <option value="recent">Recent activity</option>
            <option value="title">Title</option>
          </select>
        </label>
      </div>

      {isLoading && <p>Loading your log...</p>}
      {error && <p className="error-text">{error}</p>}
      {!isLoading && !error && entries.length === 0 && <p>You have not rated or reviewed any movies yet.</p>}

      <ul className="review-list">
        {entries.map((entry) => (
          <li key={entry.movieId} className="review-item">
            <h3><Link to={`/movies/${entry.movieId}`}>{entry.title}</Link></h3>
            <p><strong>Release year:</strong> {entry.releaseYear}</p>
            <p><strong>Your rating:</strong> {entry.rating ?? "Not rated"}</p>
            <p><strong>Your latest review:</strong> {entry.reviewText ?? "No review yet"}</p>
            {entry.containsSpoilers && <p className="spoiler-tag">Contains spoilers</p>}
          </li>
        ))}
      </ul>

      {!isLoading && !error && total > 0 && (
        <div className="pagination-row">
          <button type="button" onClick={() => setPage((value) => Math.max(1, value - 1))} disabled={page === 1}>
            Previous
          </button>
          <span>Page {page} of {totalPages}</span>
          <button type="button" onClick={() => setPage((value) => Math.min(totalPages, value + 1))} disabled={page >= totalPages}>
            Next
          </button>
        </div>
      )}
    </section>
  );
}
