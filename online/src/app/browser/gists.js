
// Copied gratefully from https://github.com/ggorlen/gist-list/blob/da3e4486111ffc27c30d38674399a74427c898d8/index.html#L135
  
class GHRequestError extends Error {
  constructor(limits = {}, ...params) {
    super(...params);

    if (Error.captureStackTrace) {
      Error.captureStackTrace(this, GHRequestError);
    }

    this.name = "GHRequestError";
    this.limits = limits;
  }
}

const fetchJson = (...args) => fetch(...args).then(async response => {
  const {headers} = response;
  const limits = {
    remaining: headers.get("x-ratelimit-remaining"),
    limit: headers.get("x-ratelimit-limit"),
    reset: new Date(headers.get("x-ratelimit-reset") * 1000),
  };

  if (!response.ok) {
    const message = response.statusText || response.status;
    throw new GHRequestError(limits, message);
  }

  return {limits, payload: await response.json()};
});

export const fetchGists = async (username, maxPages=10) =>
{
  const gists = [];
  let limits;

  for (let page = 1; page <= maxPages; page++) {
    const url = `https://api.github.com/users/${username}/gists?page=${page}`;
    const {limits: lastLimits, payload: chunk} = await fetchJson(url);
    limits = lastLimits;

    if (chunk.length === 0) {
      break;
    }
  
    gists.push(...chunk);
  }

  return {limits, gists};
};
