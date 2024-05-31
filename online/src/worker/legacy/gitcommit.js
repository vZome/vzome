

import { Octokit } from '@octokit/rest';

export const commitToGitHub = async ( token, uploads, commitMessage ) =>
{
  // There are other ways to authenticate, check https://developer.github.com/v3/#authentication
  const octo = new Octokit( { auth: token } );
  const { data: { login } } = await octo.rest.users.getAuthenticated();

  // For this, I was working on a organization repos, but it works for common repos also (replace org for owner)
  const ORGANIZATION = login;
  const REPO = 'vzome-sharing';
  const repos = await octo.repos.listForAuthenticatedUser();
  if (!repos.data.map((repo) => repo.name).includes(REPO)) {
    throw new Error( `No GitHub repo named ${ORGANIZATION}/${REPO}` );
    // await createRepo(octo, ORGANIZATION, REPO);
  }
  /**
   * my-local-folder has files on its root, and subdirectories with files
   */
  await uploadToRepo( octo, uploads, commitMessage, ORGANIZATION, REPO );
}

// const createRepo = async (octo, org, name) => {
//   await octo.repos.createInOrg({ org, name, auto_init: true })
// }

const uploadToRepo = async (
  octo,
  uploads,
  commitMessage,
  org,
  repo,
  branch = `main`
) => {
  const pathsForBlobs = uploads .map( ( { path } ) => path );
  const filesBlobs = await Promise.all( uploads .map( ( { encoding, data} ) => createBlobForFile( octo, org, repo )( data, encoding ) ) );
  // gets commit's SHA and its tree's SHA
  const currentCommit = await getCurrentCommit( octo, org, repo, branch )
  const newTree = await createNewTree(
    octo,
    org,
    repo,
    filesBlobs,
    pathsForBlobs,
    currentCommit.treeSha
  )
  const newCommit = await createNewCommit(
    octo,
    org,
    repo,
    commitMessage,
    newTree.sha,
    currentCommit.commitSha
  )
  await setBranchToCommit(octo, org, repo, branch, newCommit.sha)
}


const getCurrentCommit = async (
  octo,
  org,
  repo,
  branch = 'main'
) => {
  const { data: refData } = await octo.git.getRef({
    owner: org,
    repo,
    ref: `heads/${branch}`,
  })
  const commitSha = refData.object.sha
  const { data: commitData } = await octo.git.getCommit({
    owner: org,
    repo,
    commit_sha: commitSha,
  })
  return {
    commitSha,
    treeSha: commitData.tree.sha,
  }
}

const createBlobForFile = (octo, org, repo) => async ( content, encoding ) =>
{
  const blobData = await octo.git.createBlob({
    owner: org,
    repo,
    content,
    encoding,
  })
  return blobData.data
}

const createNewTree = async (
  octo,
  owner,
  repo,
  blobs,
  paths,
  parentTreeSha
) => {
  // My custom config. Could be taken as parameters
  const tree = blobs.map(({ sha }, index) => ({
    path: paths[index],
    mode: `100644`,
    type: `blob`,
    sha,
  }));
  const { data } = await octo.git.createTree({
    owner,
    repo,
    tree,
    base_tree: parentTreeSha,
  })
  return data
}

const createNewCommit = async (
  octo,
  org,
  repo,
  message,
  currentTreeSha,
  currentCommitSha
) =>
  (await octo.git.createCommit({
    owner: org,
    repo,
    message,
    tree: currentTreeSha,
    parents: [currentCommitSha],
  })).data

const setBranchToCommit = (
  octo,
  org,
  repo,
  branch = `main`,
  commitSha
) =>
  octo.git.updateRef({
    owner: org,
    repo,
    ref: `heads/${branch}`,
    sha: commitSha,
  })