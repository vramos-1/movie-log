import { FormEvent, useState } from "react";
import { Link } from "react-router-dom";
import { MovieSearchItem, searchMovies } from "../../../api/moviesApi";

export function MovieSearchPage() {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState<MovieSearchItem[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");
    setIsLoading(true);
    try {
      const response = await searchMovies(query);
      setResults(response);
    } catch (err) {
      setResults([]);
      setError(err instanceof Error ? err.message : "Unable to search movies");
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <section>
      <h2>Movie Search</h2>
      <form onSubmit={handleSubmit} className="search-form">
        <input
          type="text"
          value={query}
          onChange={(event) => setQuery(event.target.value)}
          placeholder="Search by title"
          minLength={2}
          required
        />
        <button type="submit" disabled={isLoading}>
          {isLoading ? "Searching..." : "Search"}
        </button>
      </form>
      {error && <p className="error-text">{error}</p>}
      {!error && results.length === 0 && <p>No results yet. Try a movie title.</p>}
      <ul className="movie-results">
        {results.map((movie) => (
          <li key={movie.id}>
            <Link to={`/movies/${movie.id}`}>
              {movie.title} ({movie.releaseYear})
            </Link>
          </li>
        ))}
      </ul>
    </section>
  );
}
